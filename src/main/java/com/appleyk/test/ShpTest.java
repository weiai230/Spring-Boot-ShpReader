package com.appleyk.test;

import com.appleyk.geotools.GeoToolsUtils;
import com.appleyk.geotools.GeometryCreator;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author: linzr
 * @Date: 2024-01-12 9:54
 * @Description: ShpTest
 */
public class ShpTest {
    static GeometryCreator gCreator = GeometryCreator.getInstance();

    @Test
    public void addFeatureFromShp() throws Exception {
        System.out.println("===============创建自己的shp文件==============");
        String MPolygonWKT = "MULTIPOLYGON(((116.3824004 39.9032955,116.3824261 39.9034733,116.382512 39.9036313,116.382718 39.9038025,116.3831643 39.903954,116.383602 39.9040198,116.3840827 39.9040001,116.3844003 39.9039211,116.3846921 39.903763,116.3848552 39.9035787,116.3848981 39.9033548,116.3848037 39.9031244,116.3845719 39.9029071,116.3842286 39.9027754,116.3837823 39.9027227,116.3833789 39.9027095,116.383027 39.902749,116.3828038 39.9028346,116.382615 39.90294,116.3824776 39.9030717,116.3824004 39.9032955)))";
        MultiPolygon multiPolygon = gCreator.createMulPolygonByWKT(MPolygonWKT);
        System.out.println(multiPolygon.getGeometryType());
        //首先得创建my这个目录
        GeoToolsUtils.writeSHP("F:/shpdata/multipol.shp", multiPolygon);
    }

    @Test
    public void modifyFeatureFromShp() throws IOException, CQLException, SchemaException {
        //获取数据源
        String filePath = "F:\\shpdata\\multipol.shp";
        File file = new File(filePath);
        ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());//创建ShapefileDataStore实例
        // 11.设置编码
        dataStore.setCharset(Charset.forName("UTF-8"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("multipol");//获取FeatureSource
        if (featureSource instanceof SimpleFeatureStore) {
            //根据条件修改
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource; // write access!
            store.modifyFeatures("name", "赤山村第一网格11-(zero)", CQL.toFilter("osm_id = '1234567890'"));
            store.modifyFeatures("des", "xxx", CQL.toFilter("osm_id = '1234567890'"));
            store.modifyFeatures("the_geom", "MULTIPOLYGON(((116.3824004 39.9032955,116.3824261 39.9034733,116.382512 39.9036313,116.382718 39.9038025,116.3831643 39.903954,116.383602 39.9040198,116.3840827 39.9040001,116.3844003 39.9039211,116.3846921 39.903763,116.3848552 39.9035787,116.3848981 39.9033548,116.3848037 39.9031244,116.3845719 39.9029071,116.3842286 39.9027754,116.3837823 39.9027227,116.3833789 39.9027095,116.383027 39.902749,116.3828038 39.9028346,116.382615 39.90294,116.3824776 39.9030717,116.3824004 39.9032955)))", CQL.toFilter("osm_id = '1234567890'"));
        }
    }

    /**
     * 追加点
     *
     * @throws IOException
     * @throws CQLException
     * @throws SchemaException
     */
    @Test
    public void appendFeatureFromShp() throws Exception {
        addFeatureFromShp();
        //获取数据源
        String filePath = "F:\\shpdata\\multipol.shp";
        File file = new File(filePath);
        ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());//创建ShapefileDataStore实例
        // 11.设置编码
        dataStore.setCharset(Charset.forName("UTF-8"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("multipol");//获取FeatureSource
        if (featureSource instanceof SimpleFeatureStore) {
            //根据条件修改
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource; // write access!
            //追加数据
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            final SimpleFeatureType TYPE = DataUtilities.createType(
                    "Location",
                    "the_geom:Point:srid=4326," +
                            "osm_id:String," +
                            "name:String," +
                            "des:String"
            );
            String name = "dsandjkadmskladakndsaldmalkdmaldkas";
            Random random = new Random();
            //创建10个新的Feature要素-[带有属性信息的简单Feature-SimpleFeature]
            List<SimpleFeature> features = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
                Point point = geometryFactory.createPoint(new Coordinate(116.382 + Math.random() * 0.001, 39.903 + Math.random() * 0.001));
                //注意字段添加顺序
                featureBuilder.add(point);
                featureBuilder.add(random.nextInt(20));
                featureBuilder.add(name.substring(0, random.nextInt(name.length())));
                //featureBuilder.set("osm_id","123");
                featureBuilder.add("zero_" + i);
                SimpleFeature simpleFeature = featureBuilder.buildFeature(String.valueOf(i));//创建Feature实例
                features.add(simpleFeature);
            }
            store.addFeatures(DataUtilities.collection(features));
        }
    }

    /**
     * 追加多边形
     *
     * @throws IOException
     * @throws CQLException
     * @throws SchemaException
     */
    @Test
    public void appendFeatureFromShp1() throws Exception {
        addFeatureFromShp();
        //获取数据源
        String filePath = "F:\\shpdata\\multipol.shp";
        File file = new File(filePath);
        ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());//创建ShapefileDataStore实例
        // 11.设置编码
        dataStore.setCharset(Charset.forName("UTF-8"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("multipol");//获取FeatureSource
        if (featureSource instanceof SimpleFeatureStore) {
            //根据条件修改
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource; // write access!
            final SimpleFeatureType TYPE = DataUtilities.createType(
                    "Location",
                    "the_geom:MultiPolygon:srid=4326," +
                            "osm_id:String," +
                            "name:String," +
                            "des:String"
            );
            String name = "dsandjkadmskladakndsaldmalkdmaldkas";
            Random random = new Random();
            //创建10个新的Feature要素-[带有属性信息的简单Feature-SimpleFeature]
            List<SimpleFeature> features = new ArrayList<>();

            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
            String MPolygonWKT = "MULTIPOLYGON(((116.3824004 39.9032955,116.3824261 39.9034733,116.382718 39.9038025,116.3824761 39.9034733,116.3824004 39.9032955)))";
            MultiPolygon multiPolygon = gCreator.createMulPolygonByWKT(MPolygonWKT);
            //注意字段添加顺序
            featureBuilder.add(multiPolygon);
            featureBuilder.add(random.nextInt(20));
            featureBuilder.add(name.substring(0, random.nextInt(name.length())));
            //featureBuilder.set("osm_id","123");
            featureBuilder.add("zero_");
            SimpleFeature simpleFeature = featureBuilder.buildFeature(null);//创建Feature实例
            features.add(simpleFeature);

            store.addFeatures(DataUtilities.collection(features));
        }
    }
}
