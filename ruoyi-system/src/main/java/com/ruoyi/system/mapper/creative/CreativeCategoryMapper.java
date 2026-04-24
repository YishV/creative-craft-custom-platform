package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeCategory;

public interface CreativeCategoryMapper
{
    CreativeCategory selectCreativeCategoryByCategoryId(Long categoryId);

    List<CreativeCategory> selectCreativeCategoryList(CreativeCategory creativeCategory);

    int insertCreativeCategory(CreativeCategory creativeCategory);

    int updateCreativeCategory(CreativeCategory creativeCategory);

    int deleteCreativeCategoryByCategoryId(Long categoryId);

    int deleteCreativeCategoryByCategoryIds(Long[] categoryIds);
}
