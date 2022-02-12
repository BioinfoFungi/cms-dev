package com.wangyang.service.base;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import com.wangyang.common.exception.FileOperationException;
import com.wangyang.common.exception.UserException;
import com.wangyang.pojo.annotation.QueryField;
import com.wangyang.pojo.enums.CrudType;
import com.wangyang.repository.base.BaseRepository;
import com.wangyang.util.File2Tsv;
import com.wangyang.util.ObjectToCollection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author wangyang
 * @date 2021/6/27
 */
public abstract class AbstractCrudService<DOMAIN, ID extends Serializable> implements ICrudService<DOMAIN, ID> {

    @PersistenceContext
    private EntityManager em;
    private final String domainName;
    private final BaseRepository<DOMAIN, ID> repository;
    public AbstractCrudService(BaseRepository<DOMAIN, ID> repository) {
        this.repository = repository;
        Class<DOMAIN> domainClass = (Class<DOMAIN>) fetchType(0);
        domainName = domainClass.getSimpleName();
    }


    @Override
    @Transactional
    public void truncateTable(){
        Entity entity = getInstance().getClass().getAnnotation(Entity.class);
        String name = entity.name();
        Query query = em.createNativeQuery("truncate table "+name);
        query.executeUpdate();
        Query resetQuery = em.createNativeQuery("ALTER TABLE "+name+" ALTER COLUMN ID RESTART WITH 1");
        resetQuery.executeUpdate();
    }

    protected Specification<DOMAIN> buildSpecByQuery(DOMAIN baseFileQuery, String keywords, Set<String> sets) {
        return (Specification<DOMAIN>) (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = toPredicate(baseFileQuery,root, query, criteriaBuilder);
            if(sets!=null && sets.size()!=0 && keywords!=null ){
                String likeCondition = String
                        .format("%%%s%%", StringUtils.strip(keywords));
                List<Predicate> orPredicates = new ArrayList<>();
                for (String filed : sets){
                    Predicate predicate = criteriaBuilder
                            .equal(root.get(filed), keywords);
                    orPredicates.add(predicate);
                }
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }

    protected List<Predicate> toPredicate(DOMAIN domain,Root<DOMAIN> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new LinkedList<>();
        try {
            List<Field> fields = ObjectToCollection.setConditionFieldList(domain);
            for(Field field : fields){
                boolean fieldAnnotationPresent = field.isAnnotationPresent(QueryField.class);
                if(fieldAnnotationPresent){
                    QueryField queryField = field.getDeclaredAnnotation(QueryField.class);
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object value = field.get(domain);
                    if(value!=null){
                        predicates.add(criteriaBuilder.equal(root.get(fieldName),value));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return predicates;
    }



    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");
        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }

    @Override
    public DOMAIN findById(ID Id){
        Optional<DOMAIN> fileOptional = repository.findById(Id);
        if(!fileOptional.isPresent()){
            throw new UserException("查找的象不存在!");
        }
        return fileOptional.get();
    }
    @Override
    public List<DOMAIN> listAll() {
//        Assert.notNull(id, domainName + " id must not be null");

        return repository.findAll();
    }

    @Override
    public DOMAIN add(DOMAIN domain) {
//        System.out.println(domainName);
        return repository.save(domain);
    }

    @Override
    public DOMAIN save(DOMAIN domain) {
        return repository.save(domain);
    }

    protected DOMAIN getInstance()
    {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<DOMAIN> type = (Class<DOMAIN>) superClass.getActualTypeArguments()[0];
        try
        {
            return type.newInstance();
        }
        catch (Exception e)
        {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(){
        repository.deleteAll();
    }
    @Override
    public void delete(DOMAIN t){
        repository.delete(t);
    }



    @Override
    public Page<DOMAIN> pageBy(Pageable pageable){
        return repository.findAll(pageable);
    }

    @Override
    public void deleteAll(Iterable<DOMAIN> domains){
        repository.deleteAll(domains);
    }

    @Override
    public  void   createTSVFile(HttpServletResponse response){
        List<DOMAIN> domains = listAll();
        String name = getInstance().getClass().getSimpleName();
        File tsvFile = createTSVFile(domains,
                "/export/"+name+".tsv", null);//CacheStore.getValue("workDir")+
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            byte[] bytes = FileUtils.readFileToByteArray(tsvFile);
            //写之前设置响应流以附件的形式打开返回值,这样可以保证前边打开文件出错时异常可以返回给前台
            response.setHeader("content-disposition","attachment;filename="+tsvFile.getName());
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public List<DOMAIN> saveAll(Iterable<DOMAIN> domain) {
        return repository.saveAll(domain);
    }

    @Override
    public File createTSVFile(List<DOMAIN> domains, String filePath,String[] heads){
        File csvFile = new File(filePath);
        File parent = csvFile.getParentFile();
        if (parent != null && !parent.exists())
        {
            parent.mkdirs();
        }
        TsvWriter writer=null;
        Writer fileWriter =null;
        try {
            TsvWriterSettings settings = new TsvWriterSettings();
            BeanWriterProcessor<DOMAIN> beanWriterProcessor = new BeanWriterProcessor<>((Class<DOMAIN>) getInstance().getClass());
            settings.setRowWriterProcessor(beanWriterProcessor);
            fileWriter = new FileWriter(csvFile);
            settings.setHeaderWritingEnabled(true);
            if(heads!=null){
                settings.setHeaders(heads);
            }
            writer = new TsvWriter(fileWriter,settings);

            writer.writeHeaders();
            writer.processRecords(domains);
            return csvFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException(e.getMessage());
        } finally {
            if(writer!=null){
                writer.close();
            }
            try {
                if(fileWriter!=null){
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<DOMAIN> tsvToBean(String filePath){
        return File2Tsv.tsvToBean((Class<DOMAIN>) getInstance().getClass(),filePath);
//        try {
//            try(FileInputStream inputStream = new FileInputStream(filePath)){
//                DOMAIN instance = getInstance();
//                BeanListProcessor<DOMAIN> beanListProcessor = new BeanListProcessor<>((Class<DOMAIN>) getInstance().getClass());
//                TsvParserSettings settings = new TsvParserSettings();
//                settings.setProcessor(beanListProcessor);
//                settings.setHeaderExtractionEnabled(true);
//                TsvParser parser = new TsvParser(settings);
//                parser.parse(inputStream);
//                List<DOMAIN> beans = beanListProcessor.getBeans();
//                inputStream.close();
//                return beans;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }


    @Override
    @Transactional
    public List<DOMAIN> initData(String filePath,Boolean isEmpty){
//        Cache cache = concurrentMapCacheManager.getCache("TERM");
//        cache.clear();
        if(isEmpty){
            truncateTable();
        }
        List<DOMAIN> beans = tsvToBean(filePath);
        if(beans==null){
            throw new FileOperationException(filePath+" 不存在！");
        }
        if(beans.size()!=0){
            beans = repository.saveAll(beans);
        }
        return beans;
    }

    @Override
    public DOMAIN delBy(ID id) {
        DOMAIN domain = findById(id);
        repository.delete(domain);
        return domain;
    }

    @Override
    public Page<DOMAIN> pageBy(Pageable pageable, String keywords) {
        return null;
    }


}
