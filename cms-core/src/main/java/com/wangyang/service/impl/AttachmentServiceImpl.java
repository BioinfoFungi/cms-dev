package com.wangyang.service.impl;

import com.wangyang.common.exception.FileOperationException;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.pojo.entity.Attachment;
import com.wangyang.pojo.enums.AttachmentType;
import com.wangyang.pojo.enums.FileWriteType;
import com.wangyang.pojo.enums.PropertyEnum;
import com.wangyang.pojo.params.AttachmentParam;
import com.wangyang.pojo.support.UploadResult;
import com.wangyang.handle.FileHandlers;
import com.wangyang.repository.AttachmentRepository;
import com.wangyang.service.IAttachmentService;
import com.wangyang.service.IOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements IAttachmentService {


    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    FileHandlers fileHandlers;

    @Autowired
    IOptionService optionService;
    @Value("${cms.workDir}")
    private String workDir;

    @Override
    public Attachment add(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Override
    public Attachment upload(MultipartFile file) {
        //TODO AttachmentType.LOCAL from databases
        UploadResult uploadResult = fileHandlers.upload(file, getAttachmentType());
        Attachment attachment = new Attachment();
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f-thumbnail.png
        attachment.setPath(uploadResult.getFilePath());
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f.png
        attachment.setFileKey(uploadResult.getKey());
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f-thumbnail.png
        attachment.setThumbPath(uploadResult.getThumbPath());
        //image/png
        attachment.setMediaType(uploadResult.getMediaType().toString());
        //png
        attachment.setSuffix(uploadResult.getSuffix());
        attachment.setWidth(uploadResult.getWidth());
        attachment.setHeight(uploadResult.getHeight());
        attachment.setSize(uploadResult.getSize());
        attachment.setType(  getAttachmentType());
        return attachmentRepository.save(attachment);
    }

    @Override
    public Attachment upload(MultipartFile file,
                             String path,
                             FileWriteType fileWriteType,
                             AttachmentType attachmentType) {
        Attachment attachment = this.findByPath(path);
        if(attachment!=null){
            if(fileWriteType.equals(FileWriteType.COVER)){
                File fileAtt = new File(workDir+File.separator+path);
                if(fileAtt.exists()){
                    fileAtt.delete();
                }
            }else if(fileWriteType.equals(FileWriteType.SKIP)){
                return attachment;
            }else {
                throw new FileOperationException("文件存在，并且STRICT");
            }
        }
        //TODO AttachmentType.LOCAL from databases
        UploadResult uploadResult = fileHandlers.upload(file,attachmentType,path);
        attachment = new Attachment();
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f-thumbnail.png
        attachment.setPath(uploadResult.getFilePath());
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f.png
        attachment.setFileKey(uploadResult.getKey());
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f-thumbnail.png
        attachment.setThumbPath(uploadResult.getThumbPath());
        //image/png
        attachment.setMediaType(uploadResult.getMediaType().toString());
        //png
        attachment.setSuffix(uploadResult.getSuffix());
        attachment.setWidth(uploadResult.getWidth());
        attachment.setHeight(uploadResult.getHeight());
        attachment.setSize(uploadResult.getSize());
        attachment.setType( attachmentType);
        return attachmentRepository.save(attachment);
    }


    @Override
    public Attachment uploadStrContent(AttachmentParam attachmentParam){
        UploadResult uploadResult = fileHandlers.uploadStrContent(attachmentParam.getFormatContent(),null, getAttachmentType());
        Attachment attachment = new Attachment();
        attachment.setOriginContent(attachmentParam.getOriginContent());
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f-thumbnail.png
        attachment.setPath(uploadResult.getFilePath());
        ///upload/2020/2/Screenshot from 2020-02-28 15-43-32-2015c76b-9442-435a-a1b7-ad030548d57f.png
        attachment.setFileKey(uploadResult.getKey());
        //image/png
//        attachment.setMediaType(uploadResult.getMediaType().toString());
        //png
        attachment.setRenderType(attachmentParam.getRenderType());

        attachment.setSuffix(uploadResult.getSuffix());
        attachment.setName(uploadResult.getFilename());
        attachment.setSize(uploadResult.getSize());
        attachment.setType( getAttachmentType());
        return attachmentRepository.save(attachment);
    }

    @Override
    public Attachment uploadStrContent(int attachmentId,AttachmentParam attachmentParam){
        Attachment attachment = findById(attachmentId);

        UploadResult uploadResult = fileHandlers.uploadStrContent(attachmentParam.getFormatContent(),attachment.getName(), getAttachmentType());

        attachment.setOriginContent(attachmentParam.getOriginContent());
        attachment.setRenderType(attachmentParam.getRenderType());
        attachment.setSize(uploadResult.getSize());
        attachment.setType( getAttachmentType());
        return attachmentRepository.save(attachment);
    }



    @Override
    public Page<Attachment> list(Pageable pageable){
        return attachmentRepository.findAll(pageable);
    }

    @Override
    public Attachment deleteById(int id){
        Attachment attachment = findById(id);
        fileHandlers.delete(attachment);
        attachmentRepository.deleteById(id);
        return attachment;
    }
    @Override
    public List<Attachment> deleteByIds(Collection<Integer> ids){
        return ids.stream().map(this::deleteById).collect(Collectors.toList());
    }

    @Override
    public Attachment findById(int id){
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if(!optionalAttachment.isPresent()){
            throw  new ObjectException("Attachment not found!!");
        }
        return optionalAttachment.get();
    }

    public Attachment findByPath(String path){
        List<Attachment> attachments = attachmentRepository.findAll(new Specification<Attachment>() {
            @Override
            public Predicate toPredicate(Root<Attachment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("path"),path)).getRestriction();
            }
        });
        return attachments.size()!=0?attachments.get(0):null;
    }

    @Override
    public AttachmentType getAttachmentType(){
        String propertyValue = optionService.getPropertyStringValue(PropertyEnum.ATTACHMENT_TYPE);
        if(propertyValue==null){
            return AttachmentType.LOCAL;
        }
        return AttachmentType.valueOf(propertyValue);
    }
}
