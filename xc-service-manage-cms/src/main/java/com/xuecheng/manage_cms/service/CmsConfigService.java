package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsConfigService {

    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    /**
     * 按ID查询CmsConfig
     *
     * @param id ID
     * @return CmsConfig
     */
    public CmsConfig findCmsConfigById(String id){
        Optional<CmsConfig> one = cmsConfigRepository.findById(id);
        if(one.isPresent()) return one.get();
        return null;
    }
}
