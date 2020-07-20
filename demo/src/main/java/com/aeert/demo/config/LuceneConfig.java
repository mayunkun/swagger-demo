package com.aeert.demo.config;/**
 * Created by Administrator on 2019/11/12/012.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @Author l'amour solitaire
 * @Description Lucene
 * @Date 2020/7/17 下午1:46
 **/
@Configuration
public class LuceneConfig {

    /**
     * lucene索引,存放位置
     **/
    public static final String LUCENE_INDEX_PATH = "/Users/mayunkun/Desktop/index/";

    /**
     * 创建一个 Analyzer 实例
     */
    @Bean
    public Analyzer analyzer() {
//        CharArraySet charArraySet = new CharArraySet(0, true);
//        // 系统默认停用词
//        Iterator<Object> iterator = SmartChineseAnalyzer.getDefaultStopSet().iterator();
//        while (iterator.hasNext()) {
//            charArraySet.add(iterator.next());
//        }
//        // 自定义停用词
//        String[] myStopWords = {"华为"};
//        for (String stopWord : myStopWords) {
//            charArraySet.add(stopWord);
//        }
//        return new SmartChineseAnalyzer(charArraySet);
        return new SmartChineseAnalyzer();
    }

    /**
     * 索引位置
     */
    @Bean
    public Directory directory() throws IOException {

        Path path = Paths.get(LUCENE_INDEX_PATH);
        File file = path.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        return FSDirectory.open(path);
    }

    /**
     * 创建indexWriter
     */
    @Bean
    public IndexWriter indexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        /**
         * 清空索引,下面两个方法，打开时，启动应用时，会自动清空所有索引，
         * 如果项目的数据比较多，建索引时间长或索引不能清空，这个一定要装着
         * 所以大家要根据实际情况慎用此开关
         */
        indexWriter.deleteAll();
        indexWriter.commit();
        return indexWriter;
    }

    /**
     * SearcherManager管理
     */
    @Bean
    public SearcherManager searcherManager(Directory directory, IndexWriter indexWriter) throws IOException {
        SearcherManager searcherManager = new SearcherManager(indexWriter, false, false, new SearcherFactory());
        ControlledRealTimeReopenThread cRTReopenThead = new ControlledRealTimeReopenThread(indexWriter, searcherManager,
                5.0, 0.025);
        cRTReopenThead.setDaemon(true);
        //线程名称
        cRTReopenThead.setName("更新IndexReader线程");
        // 开启线程
        cRTReopenThead.start();
        return searcherManager;
    }

}
