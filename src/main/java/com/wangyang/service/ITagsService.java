package com.wangyang.service;

import com.wangyang.pojo.dto.TagsDto;
import com.wangyang.pojo.entity.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITagsService {

    /**
     * list tags and calculate quantity
     * @param pageable
     * @return
     */
    Page<TagsDto> list(Pageable pageable);

    /**
     * add Tags
     * @param tags
     * @return
     */
    Tags add(Tags tags);

    /**
     * update tags by Id
     * @param id
     * @return
     */
    Tags update(int id, Tags tagsUpdate);

    /**
     * delete tags by id
     * @param id
     */
    void deleteById(int id);

    /**
     * find tags by id
     * @param id
     * @return
     */
    Tags findById(int id);

    List<TagsDto> listAll();

    Optional<Tags> findBy(String tagName);

    Optional<Tags> findBySlugName(String slugName);
}
