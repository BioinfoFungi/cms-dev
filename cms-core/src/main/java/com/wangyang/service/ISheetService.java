package com.wangyang.service;

import com.wangyang.pojo.entity.Sheet;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.pojo.vo.SheetDetailVO;
import com.wangyang.pojo.vo.SheetVo;
import com.wangyang.service.base.IContentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISheetService extends IContentService<Sheet,Sheet, ContentVO> {

    List<Sheet> listAll();
    Sheet save(Sheet sheet);

//    List<SheetDto> findListByChannelId(int channelId);

    void deleteAll();

    Sheet addOrUpdate(Sheet sheet);

    Sheet findById(int id);

    Sheet update(Sheet updateSheet);

    Sheet deleteById(int id);



//    SheetDetailVo getSheetVoById(int id);

//    String generateHtml(int id);

    Page<Sheet> list(Pageable pageable);

    Page<SheetVo> conventTo(Page<Sheet> sheetPage);

    Sheet addOrRemoveToMenu(int id);

    SheetDetailVO findDetailVOByViewName(String viewName);

//    Page<SheetVo> conventTo(Page<Sheet> sheetPage);
}
