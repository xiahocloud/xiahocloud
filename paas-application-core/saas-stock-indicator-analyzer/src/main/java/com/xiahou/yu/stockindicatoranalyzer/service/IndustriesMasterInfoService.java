package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndustriesMasterInfo;
import com.xiahou.yu.stockindicatoranalyzer.repository.IndustriesMasterInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndustriesMasterInfoService {

    private final IndustriesMasterInfoRepository repository;

    public IndustriesMasterInfo save(IndustriesMasterInfo info) {
        return repository.save(info);
    }

    public Optional<IndustriesMasterInfo> findByCode(String industryCode) {
        return repository.findByIndustryCode(industryCode);
    }

    public Optional<IndustriesMasterInfo> findByName(String industryName) {
        return repository.findByIndustryName(industryName);
    }

    public List<IndustriesMasterInfo> list(Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listAll(s, offset);
    }

    public long count() {
        return repository.countAll();
    }

    public List<IndustriesMasterInfo> list(String status, String level, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listWithFilters(status, level, s, offset);
    }

    public long count(String status, String level) {
        return repository.countWithFilters(status, level);
    }
}