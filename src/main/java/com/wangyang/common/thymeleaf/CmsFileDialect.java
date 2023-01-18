package com.wangyang.common.thymeleaf;

import com.wangyang.service.IComponentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor;
import org.thymeleaf.standard.processor.StandardInliningCDATASectionProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wangyang
 * @date 2020/12/17
 */
@Service
public class CmsFileDialect extends AbstractProcessorDialect {


    @Autowired
    IComponentsService componentsService;

    //定义方言名称
    private static final String DIALECT_NAME = "Score Dialect";
    public CmsFileDialect() {
        //设置自定义方言与"方言处理器"优先级相同
        super(DIALECT_NAME, "cms", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new CmsFileReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
        //   let viewName = String([[${view.category.viewName}]]) 解析出来会有引号
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.HTML));
        processors.add(new CmsFileStandardInlineHTMLTagProcessor(dialectPrefix));
        processors.add(new CmsClassappendTagProcessor(dialectPrefix));


//        processors.add(new StandardTextTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardValueTagProcessor(dialectPrefix));
//        processors.add(new StandardReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardIfTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new StandardFragmentTagProcessor(TemplateMode.HTML, dialectPrefix));
//        processors.add(new IncludeElementTagProcessor(dialectPrefix));
        return processors;
    }
}
