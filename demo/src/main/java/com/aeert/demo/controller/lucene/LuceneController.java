package com.aeert.demo.controller.lucene;

import com.aeert.demo.annotation.ApiVersion;
import com.aeert.demo.entity.Article;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vavr.Function1;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author l'amour solitaire
 * @Description Lucene测试
 * @Date 2020/7/16 上午9:31
 **/
@RestController
@RequestMapping("/api/{version}/")
@Api(tags = "Lucene测试")
public class LuceneController {

    /**
     * 写索引实例
     */
    @Autowired
    private IndexWriter indexWriter;

    @Autowired
    private Analyzer analyzer;

    @Autowired
    private SearcherManager searcherManager;

    @ApiVersion(1)
    @GetMapping("searchAll")
    @ApiOperation("检索ALL")
    public List<Article> searchAll(@PathVariable String version, @RequestParam String key) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
        searcherManager.maybeRefresh();
        IndexSearcher indexSearcher = searcherManager.acquire();
        Query query = new MatchAllDocsQuery();
        TopDocs topDocs = indexSearcher.search(query, 10);

        List<Article> articleList = Arrays.asList(topDocs.scoreDocs).stream().map(m -> {
            try {
                Document doc = indexSearcher.doc(m.doc);
                return Article.builder()
                        .id(Long.valueOf(doc.get("id")))
                        .authorName(doc.get("authorName"))
                        .title(doc.get("title"))
                        .content(doc.get("content"))
                        .createTime(stringToDateFormatter.apply(doc.get("createTime")))
                        .build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        return articleList;
    }

    @ApiVersion(1)
    @GetMapping("search")
    @ApiOperation("检索")
    public List<Article> search(@PathVariable String version, @RequestParam String key) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
        searcherManager.maybeRefresh();
        IndexSearcher indexSearcher = searcherManager.acquire();

        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        //  1．MUST和MUST：取得连个查询子句的交集。
        //  2．MUST和MUST_NOT：表示查询结果中不能包含MUST_NOT所对应得查询子句的检索结果。
        // 3．SHOULD与MUST_NOT：连用时，功能同MUST和MUST_NOT。
        // 4．SHOULD与MUST连用时，结果为MUST子句的检索结果,但是SHOULD可影响排序。
        // 5．SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集。
        // 6．MUST_NOT和MUST_NOT：无意义，检索无结果。

        // 精确匹配
        builder.add(new TermQuery(new Term("title", key)), BooleanClause.Occur.SHOULD);
        builder.add(new TermQuery(new Term("content", key)), BooleanClause.Occur.SHOULD);

        // 模糊匹配
//        builder.add(new QueryParser("content", analyzer).parse(key), BooleanClause.Occur.MUST);

        // 排序
        Sort sort = new Sort();
        sort.setSort(new SortField("authorName", SortField.Type.STRING, false));

        TopDocs topDocs = indexSearcher.search(builder.build(), 10, sort);


//        //根据关键字构造一个数组
//        String[] keys = new String[]{key, key};
//        //同时声明一个与之对应的字段数组
//        String[] fields = {"title", "content"};
//        //声明BooleanClause.Occur[]数组,它表示多个条件之间的关系
//        BooleanClause.Occur[] flags = new BooleanClause.Occur[]{BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
//        SmartChineseAnalyzer analyzer1 = new SmartChineseAnalyzer();
//        //用MultiFieldQueryParser得到query对象
//        Query query = MultiFieldQueryParser.parse(key, fields, flags, analyzer1);
//        // 排序
//        Sort sort = new Sort();
//        sort.setSort(new SortField("authorName", SortField.Type.STRING, false));
//
//        TopDocs topDocs = indexSearcher.search(query, 10, sort);


//        //如果不指定参数的话，默认是加粗，即 <b><b/>
//        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=red>","</font></b>");
//        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, topDocs);
//        //根据这个得分计算出一个片段
//        Fragmenter fragmenter = new SimpleSpanFragmenter(builder);
//        //设置一下要显示的片段
//        highlighter.setTextFragmenter(fragmenter);

        List<Article> articleList = Arrays.asList(topDocs.scoreDocs).stream().map(m -> {
            try {
                Document doc = indexSearcher.doc(m.doc);
                return Article.builder()
                        .id(Long.valueOf(doc.get("id")))
                        .authorName(doc.get("authorName"))
                        .title(doc.get("title"))
                        .content(doc.get("content"))
                        .createTime(stringToDateFormatter.apply(doc.get("createTime")))
                        .build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        return articleList;
    }

    @ApiVersion(1)
    @PostMapping("install")
    @ApiOperation("初始化")
    public boolean install(@PathVariable String version) throws IOException {
        // 模拟数据库数据查询
        List<Article> articleList = new ArrayList<Article>(Arrays.asList(
                Article.builder().id(100L).title("国企挺住！").content("希望华为早日突破封锁，中国早日突破围困！").authorName("任正非").createTime(new Date()).build()
                , Article.builder().id(101L).title("建造者模式").content("Builder 使用创建者模式又叫建造者模式。简单来说，就是一步步创建一个对象，它对用户屏蔽了里面构建的细节，但却可以精细地控制对象的构造过程。").authorName("java菜鸟").createTime(new Date()).build()
                , Article.builder().id(102L).title("华为获得大量融资").content("华为获得大量融资！阿里，腾讯等均参与！").authorName("java菜鸟").createTime(new Date()).build()
                , Article.builder().id(103L).title("核心技术教程").content("Fork/Join框架是Java7提供的并行执行任务框架，思想是将大任务分解成小任务，然后小任务又可以继续分解，然后每个小任务分别计算出结果再合并起来，最后将汇总的结果作为大任务结果。其思想和MapReduce的思想非常类似。对于任务的分割，要求各个子任务之间相互独立，能够并行独立地执行任务，互相之间不影响。").authorName("java菜鸟").createTime(new Date()).build()
                , Article.builder().id(104L).title("华为").content("活锁这个概念大家应该很少有人听说或理解它的概念，而在多线程中这确实存在。活锁恰恰与死锁相反，死锁是大家都拿不到资源都占用着对方的资源，而活锁是拿到资源却又相互释放不执行。当多线程中出现了相互谦让，都主动将资源释放给别的线程使用，这样这个资源在多个线程之间跳动而又得不到执行，这就是活锁。").authorName("java菜鸟").createTime(new Date()).build()
        ));
        List<Document> documentList = new ArrayList<>(articleList.size());
        articleList.forEach(m -> {
            Document document = new Document();
            document.add(new StoredField("id", m.getId()));
            document.add(new StringField("title", m.getTitle(), Field.Store.YES));
            document.add(new TextField("content", m.getContent(), Field.Store.YES));
            document.add(new StringField("authorName", m.getAuthorName(), Field.Store.YES));
            document.add(new StoredField("createTime", dateToStringFormatter.apply(m.getCreateTime())));

            // 添加排序
            document.add(new SortedDocValuesField("authorName", new BytesRef(m.getAuthorName())));
            documentList.add(document);

        });
        indexWriter.addDocuments(documentList);
        indexWriter.commit();
        return true;
    }

    @ApiVersion(1)
    @PostMapping("add")
    @ApiOperation("新增")
    public boolean add(@PathVariable String version, @RequestBody Article article) {
        return true;
    }

    @ApiVersion(1)
    @PostMapping("update")
    @ApiOperation("编辑")
    public boolean update(@PathVariable String version, @RequestBody Article article) {
        return true;
    }

    @ApiVersion(1)
    @PostMapping("delete")
    @ApiOperation("删除")
    public boolean delete(@PathVariable String version, @RequestBody Article article) {
        return true;
    }

    Function1<String, Date> stringToDateFormatter = k -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(k);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    };

    Function1<Date, String> dateToStringFormatter = k -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(k);
    };

}
