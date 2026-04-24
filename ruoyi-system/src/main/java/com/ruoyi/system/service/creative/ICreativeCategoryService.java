package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeCategory;

public interface ICreativeCategoryService
{
    CreativeCategory selectCreativeCategoryByCategoryId(Long categoryId);

    List<CreativeCategory> selectCreativeCategoryList(CreativeCategory creativeCategory);

    int insertCreativeCategory(CreativeCategory creativeCategory);

    int updateCreativeCategory(CreativeCategory creativeCategory);

    int deleteCreativeCategoryByCategoryId(Long categoryId);

    int deleteCreativeCategoryByCategoryIds(Long[] categoryIds);
}
