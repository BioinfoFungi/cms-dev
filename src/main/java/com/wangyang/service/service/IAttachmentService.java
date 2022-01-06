package com.wangyang.service.service;

import com.wangyang.pojo.entity.Attachment;
import com.wangyang.pojo.enums.AttachmentType;
import com.wangyang.pojo.enums.FileWriteType;
import com.wangyang.pojo.params.AttachmentParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface IAttachmentService {
    Attachment add(Attachment attachment);
    Attachment upload(@NonNull MultipartFile file);

    Attachment upload(MultipartFile file, String path, FileWriteType fileWriteType, AttachmentType attachmentType);

    Attachment uploadStrContent(AttachmentParam attachmentParam);

    Attachment uploadStrContent(int attachmentId, AttachmentParam attachmentParam);

    Page<Attachment> list(Pageable pageable);

    Attachment deleteById(int id);

    List<Attachment> deleteByIds(Collection<Integer> ids);

    Attachment findById(int id);

    AttachmentType getAttachmentType();
}
