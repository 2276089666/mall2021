package com.cloud.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.cloud.common.to.es.SkuEsModel;
import com.cloud.mall.search.config.ElasticsearchConfig;
import com.cloud.mall.search.constant.EsConstant;
import com.cloud.mall.search.service.MallSearchService;
import com.cloud.mall.search.vo.SearchParamVo;
import com.cloud.mall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ws
 * @Date 2021/3/11 19:44
 * @Version 1.0
 */
@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        //1.商品检索DSL productSearchDSL.json
        SearchResponseVo searchResponseVo = null;
        //2.编写检索请求
        SearchRequest searchRequest = buildSearchRequest(searchParamVo);
        try {
            //3.执行检索请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
            //4.封装结果
            searchResponseVo = buildSearchResponseVo(searchResponse,searchParamVo);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return searchResponseVo;
    }

    /**
     * 构建结果
     *
     * @param searchResponse
     * @return
     */
    private SearchResponseVo buildSearchResponseVo(SearchResponse searchResponse,SearchParamVo searchParamVo) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();

        if (hits!=null&&hits.getHits().length>0){
            List<SkuEsModel> skuEsModelList = Arrays.stream(hits.getHits()).map(a -> {
                String sourceAsString = a.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(searchParamVo.getKeyword())){
                    HighlightField skuTitle = a.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(string);
                }
                return skuEsModel;
            }).collect(Collectors.toList());
            searchResponseVo.setSkuEsModelList(skuEsModelList);
        }

        ParsedLongTerms catalog_agg =searchResponse.getAggregations().get("catalog_agg");
        if (catalog_agg.getBuckets()!=null&&!catalog_agg.getBuckets().isEmpty()){
            List<SearchResponseVo.CatalogVo> catalogVoList = catalog_agg.getBuckets().stream().map(b -> {
                SearchResponseVo.CatalogVo catalogVo = new SearchResponseVo.CatalogVo();
                catalogVo.setCatalogId(b.getKeyAsNumber().longValue());
                ParsedStringTerms catalog_name_agg = b.getAggregations().get("catalog_name_agg");
                String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();
                catalogVo.setCatalogName(catalogName);
                return catalogVo;
            }).collect(Collectors.toList());
            searchResponseVo.setCatalogVos(catalogVoList);
        }


        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        if (brand_agg.getBuckets()!=null&&!brand_agg.getBuckets().isEmpty()){
            List<SearchResponseVo.BrandVo> brandVoList = brand_agg.getBuckets().stream().map(c -> {
                SearchResponseVo.BrandVo brandVo = new SearchResponseVo.BrandVo();
                brandVo.setBrandId(c.getKeyAsNumber().longValue());
                ParsedStringTerms brand_img_agg = c.getAggregations().get("brand_img_agg");
                List<? extends Terms.Bucket> buckets = brand_img_agg.getBuckets();
                if (!buckets.isEmpty()) {
                    String img = buckets.get(0).getKeyAsString();
                    brandVo.setBrandImg(img);
                }
                ParsedStringTerms brand_name_agg = c.getAggregations().get("brand_name_agg");
                List<? extends Terms.Bucket> brandNameAggBuckets = brand_name_agg.getBuckets();
                if (!brandNameAggBuckets.isEmpty()) {
                    String brandName = brandNameAggBuckets.get(0).getKeyAsString();
                    brandVo.setBrandName(brandName);
                }

                return brandVo;
            }).collect(Collectors.toList());
            searchResponseVo.setBrandVos(brandVoList);
        }

        ParsedNested attr_agg =searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg=attr_agg.getAggregations().get("attr_id_agg");
        ArrayList<SearchResponseVo.AttrVo> attrVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = attr_id_agg.getBuckets();
        if (buckets!=null&&!buckets.isEmpty()){
            for (Terms.Bucket bucket : buckets) {
                SearchResponseVo.AttrVo attrVo = new SearchResponseVo.AttrVo();
                attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
                ParsedStringTerms attr_name_agg=bucket.getAggregations().get("attr_name_agg");
                attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());
                ParsedStringTerms attr_value_agg=bucket.getAggregations().get("attr_value_agg");
                if (attr_value_agg.getBuckets()!=null&&!attr_value_agg.getBuckets().isEmpty()){
                    List<String> attrValues = attr_value_agg.getBuckets().stream().map(d -> {
                        return d.getKeyAsString();
                    }).collect(Collectors.toList());
                    attrVo.setAttrValue(attrValues);
                }
                attrVos.add(attrVo);
            }
            searchResponseVo.setAttrVos(attrVos);
        }

        long total = hits.getTotalHits().value;
        searchResponseVo.setTotal(total);
        searchResponseVo.setTotalPages(total%EsConstant.PRODUCT_PAGE_SIZE==0?(int)total/EsConstant.PRODUCT_PAGE_SIZE:(int)(total/EsConstant.PRODUCT_PAGE_SIZE+1));
        searchResponseVo.setPageNum(searchParamVo.getPageNumber());

        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (Integer i = 1; i <= searchResponseVo.getTotalPages(); i++) {
            pageNavs.add(i);
        }
        searchResponseVo.setPageNavs(pageNavs);

        return searchResponseVo;
    }

    /**
     * 创建检索请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParamVo searchParamVo) {

        SearchRequest searchRequest = new SearchRequest();
        // 设置检索请求索引
        searchRequest.indices(EsConstant.PRODUCT_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // must 标题
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParamVo.getKeyword()));
        }
        /**
         * filter
         */
        // term 三级分类id
        if (searchParamVo.getCatalog3Id()!=null&&searchParamVo.getCatalog3Id() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParamVo.getCatalog3Id()));
        }
        // terms 品牌id
        if (searchParamVo.getBrandId() != null && !searchParamVo.getBrandId().isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParamVo.getBrandId()));
        }
        // nested 嵌入式属性
        /**
         *  每一个属性id对应一个NestedQuery
         *
         *  attrs=1_3G:4G:5G&attrs=2_骁龙 845&attrs=4_高清屏
         */
        if (searchParamVo.getAttrs()!=null&&!searchParamVo.getAttrs().isEmpty()){
            for (String attr : searchParamVo.getAttrs()) {
                String[] s = attr.split("_");
                String attrId=s[0];
                String[] attrValues = s[1].split(":");
                BoolQueryBuilder builder = QueryBuilders.boolQuery();
                builder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                builder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                //ScoreMode.None 不参与评分
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",builder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        // term 是否有库存
        if (searchParamVo.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("hasStock", searchParamVo.getHasStock() == 1));
        }

        // range 价格区间
        /**
         * skuPrice=400_1900  400到1900之间
         * skuPrice=_400      400以下
         * skuPrice=1900_     1900以上
         */
        if (!StringUtils.isEmpty(searchParamVo.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParamVo.getSkuPrice().split("_");
            if (s.length==2){
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            }else if (s.length==1){
                if (searchParamVo.getSkuPrice().startsWith("_")){
                    rangeQueryBuilder.lte(s[0]);
                }
                if (searchParamVo.getSkuPrice().endsWith("_")){
                    rangeQueryBuilder.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        // 查询
        searchSourceBuilder.query(boolQueryBuilder);

        // 排序  sort=saleCount_desc
        if (!StringUtils.isEmpty(searchParamVo.getSort())){
            String[] s = searchParamVo.getSort().split("_");
            SortOrder sortOrder=s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(s[0],sortOrder);
        }

        // 分页  (pageNumber-1)*pageSize
        searchSourceBuilder.from((searchParamVo.getPageNumber()-1)*EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        // 高亮
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        // 1.聚合
        // 1.1品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(10);
        // 1.1.1品牌聚合的子聚合,品牌名和品牌图片聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);

        // 1.2三级分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(10);
        // 1.2.1三级分类的子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);

        // 1.3属性嵌入式聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
        // 1.3.1子聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        nested.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(nested);

        log.info("商品检索的DSL:\t{}",searchSourceBuilder.toString());
        return searchRequest.source(searchSourceBuilder);
    }
}
