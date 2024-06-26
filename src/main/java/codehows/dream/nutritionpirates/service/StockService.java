package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.Stock;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StockService {

    private final StockRepository stockRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

   /* private Order findOrderByStockId(Long stockId) {
        // Custom query in StockService to find Order by Stock id
        return orderRepository.findByStockId(stockId); // Using custom query method in OrderRepository
    }
    public List<StockShowDTO> getStockList(Pageable pageable) {
        List<StockShowDTO> list = new ArrayList<>();
        Page<Stock> pages = stockRepository.findAll(pageable);

        Order order;

        pages.forEach((e) -> {
            Stock stock = stockRepository.findById(e.getId()).orElse(null);
            //Order order = findOrderByStockId(e.getId());

           // if (order != null) {


            list.add(StockShowDTO.builder()
                            .id(e.getId())
                            .quantity(e.getQuantity())
                            .createDate(e.getCreateDate())
                            .exportDate(e.getExportDate())
                            .isExport(e.isExport())
                            .build());
           // }
        });

        return list;
    }*/

}
