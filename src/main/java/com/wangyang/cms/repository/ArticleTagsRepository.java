package com.wangyang.cms.repository;

import com.wangyang.cms.pojo.entity.ArticleCategory;
import com.wangyang.cms.pojo.entity.ArticleTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ArticleTagsRepository extends JpaRepository<ArticleTags,Integer> {

    List<ArticleTags> findAllByArticleIdIn(Collection<Integer> articleIds);
    List<ArticleTags> findByArticleId(int articleId);

    List<ArticleTags> deleteByArticleId(int id);

}
