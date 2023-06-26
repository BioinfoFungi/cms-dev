package com.wangyang.repository;

import com.wangyang.pojo.entity.ArticleTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ArticleTagsRepository extends JpaRepository<ArticleTags,Integer> {

    List<ArticleTags> findAllByArticleIdIn(Collection<Integer> articleIds);
    List<ArticleTags> findAllByTagsIdIn(Collection<Integer> tagsIds);
    List<ArticleTags> findByArticleId(int articleId);
    List<ArticleTags> findByTagsId(int tagsId);

    List<ArticleTags> deleteByArticleId(int id);



}
