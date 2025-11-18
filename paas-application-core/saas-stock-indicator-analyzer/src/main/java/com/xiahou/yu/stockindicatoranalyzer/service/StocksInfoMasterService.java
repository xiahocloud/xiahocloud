package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksInfoMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StocksInfoMasterService {
    private final StocksInfoMasterRepository repository;

    public StocksInfoMaster save(StocksInfoMaster info) {
        return repository.save(info);
    }

    public Optional<StocksInfoMaster> findByCode(String code) {
        return repository.findByStockCode(code);
    }
}

