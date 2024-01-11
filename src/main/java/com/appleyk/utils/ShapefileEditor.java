package com.appleyk.utils;

import org.geotools.api.data.FeatureSource;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.Name;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ShapefileEditor {
    static GeometryFactory geometryFactory = new GeometryFactory();
    static FeatureSource featureSource = null;

    public static void main(String[] args) throws IOException, CQLException {
        //获取数据源
        String filePath = "F:\\shpdata\\单元网格1-1.shp";
        File file = new File(filePath);
        ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());//创建ShapefileDataStore实例
        // 11.设置编码
        dataStore.setCharset(Charset.forName("GBK"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource("单元网格1-1");//获取FeatureSource
        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource; // write access!
            store.modifyFeatures("name", "zero", CQL.toFilter("name = 'xxx'"));
            SimpleFeatureCollection features = featureSource.getFeatures();
            SimpleFeatureIterator iterator = features.features();
            System.out.println(features.size());
            while (iterator.hasNext()) {
                SimpleFeature next = iterator.next();
                SimpleFeatureType featureType = next.getFeatureType();
                List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
                for (int i1 = 0; i1 < attributeDescriptors.size(); i1++) {
                    Name name = attributeDescriptors.get(i1).getName();
                    System.out.print(name + ":" + next.getAttribute(name) + "\t");
                }
                System.out.println();
            }
        }
    }
}
