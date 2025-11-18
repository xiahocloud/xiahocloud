package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesMasterInfo;
import com.xiahou.yu.stockindicatoranalyzer.repository.IndexesMasterInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndexesMasterInfoService {

    private final IndexesMasterInfoRepository repository;

    public IndexesMasterInfo saveOrUpdate(IndexesMasterInfo info) {
        Optional<IndexesMasterInfo> existing = repository.findByIndexCode(info.getIndexCode());
        if (existing.isPresent()) {
            IndexesMasterInfo old = existing.get();
            info.setId(old.getId());
            info.setCreatedAt(old.getCreatedAt());
            info.setUpdatedAt(LocalDateTime.now());
        }
        return repository.save(info);
    }

    public Optional<IndexesMasterInfo> findByCode(String indexCode) {
        return repository.findByIndexCode(indexCode);
    }

    public Optional<IndexesMasterInfo> findByName(String indexName) {
        return repository.findByIndexName(indexName);
    }

    public List<IndexesMasterInfo> list(int pageNum, int pageSize) {
        int s = pageSize <= 0 ? 50 : pageSize;
        int p = pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listAll(s, offset);
    }

    public long count() {
        return repository.countAll();
    }
}