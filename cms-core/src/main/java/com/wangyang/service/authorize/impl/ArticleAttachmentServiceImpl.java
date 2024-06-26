package com.wangyang.service.authorize.impl;

import com.wangyang.pojo.entity.ArticleAttachment;
import com.wangyang.common.enums.CrudType;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.repository.authorize.ArticleAttachmentRepository;
import com.wangyang.service.authorize.IArticleAttachmentService;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleAttachmentServiceImpl extends AbstractCrudService<ArticleAttachment,ArticleAttachment, BaseVo,Integer>
        implements IArticleAttachmentService {

    private ArticleAttachmentRepository articleAttachmentRepository;
    public ArticleAttachmentServiceImpl(ArticleAttachmentRepository articleAttachmentRepository) {
        super(articleAttachmentRepository);
        this.articleAttachmentRepository=articleAttachmentRepository;
    }

    @Override
    public List<ArticleAttachment> findByTemplateId(Integer id) {
        return articleAttachmentRepository.findByTemplateId(id);
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }
}
