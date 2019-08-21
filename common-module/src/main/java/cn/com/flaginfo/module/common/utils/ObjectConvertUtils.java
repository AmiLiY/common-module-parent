package cn.com.flaginfo.module.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.xml.sax.InputSource;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Slf4j
public class ObjectConvertUtils {

    private static final Map<Class, PropertyEditor> PROPERTY_EDITOR_MAP = new HashMap<>();

    static {
        PROPERTY_EDITOR_MAP.put(Charset.class, new CharsetEditor());
        PROPERTY_EDITOR_MAP.put(Class.class, new ClassEditor());
        PROPERTY_EDITOR_MAP.put(Class[].class, new ClassArrayEditor());
        PROPERTY_EDITOR_MAP.put(Currency.class, new CurrencyEditor());
        PROPERTY_EDITOR_MAP.put(File.class, new FileEditor());
        PROPERTY_EDITOR_MAP.put(InputStream.class, new InputStreamEditor());
        PROPERTY_EDITOR_MAP.put(InputSource.class, new InputSourceEditor());
        PROPERTY_EDITOR_MAP.put(Locale.class, new LocaleEditor());
        PROPERTY_EDITOR_MAP.put(Path.class, new PathEditor());
        PROPERTY_EDITOR_MAP.put(Pattern.class, new PatternEditor());
        PROPERTY_EDITOR_MAP.put(Properties.class, new PropertiesEditor());
        PROPERTY_EDITOR_MAP.put(Reader.class, new ReaderEditor());
        PROPERTY_EDITOR_MAP.put(Resource[].class, new ResourceArrayPropertyEditor());
        PROPERTY_EDITOR_MAP.put(TimeZone.class, new TimeZoneEditor());
        PROPERTY_EDITOR_MAP.put(URI.class, new URIEditor());
        PROPERTY_EDITOR_MAP.put(URL.class, new URLEditor());
        PROPERTY_EDITOR_MAP.put(UUID.class, new UUIDEditor());
        PROPERTY_EDITOR_MAP.put(ZoneId.class, new ZoneIdEditor());

        PROPERTY_EDITOR_MAP.put(Collection.class, new CustomCollectionEditor(Collection.class));
        PROPERTY_EDITOR_MAP.put(Set.class, new CustomCollectionEditor(Set.class));
        PROPERTY_EDITOR_MAP.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
        PROPERTY_EDITOR_MAP.put(List.class, new CustomCollectionEditor(List.class));
        PROPERTY_EDITOR_MAP.put(SortedMap.class, new CustomMapEditor(SortedMap.class));

        PROPERTY_EDITOR_MAP.put(byte[].class, new ByteArrayPropertyEditor());
        PROPERTY_EDITOR_MAP.put(char[].class, new CharArrayPropertyEditor());

        PROPERTY_EDITOR_MAP.put(char.class, new CharacterEditor(false));
        PROPERTY_EDITOR_MAP.put(Character.class, new CharacterEditor(true));

        PROPERTY_EDITOR_MAP.put(boolean.class, new CustomBooleanEditor(false));
        PROPERTY_EDITOR_MAP.put(Boolean.class, new CustomBooleanEditor(true));

        PROPERTY_EDITOR_MAP.put(byte.class, new CustomNumberEditor(Byte.class, false));
        PROPERTY_EDITOR_MAP.put(Byte.class, new CustomNumberEditor(Byte.class, true));
        PROPERTY_EDITOR_MAP.put(short.class, new CustomNumberEditor(Short.class, false));
        PROPERTY_EDITOR_MAP.put(Short.class, new CustomNumberEditor(Short.class, true));
        PROPERTY_EDITOR_MAP.put(int.class, new CustomNumberEditor(Integer.class, false));
        PROPERTY_EDITOR_MAP.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        PROPERTY_EDITOR_MAP.put(long.class, new CustomNumberEditor(Long.class, false));
        PROPERTY_EDITOR_MAP.put(Long.class, new CustomNumberEditor(Long.class, true));
        PROPERTY_EDITOR_MAP.put(float.class, new CustomNumberEditor(Float.class, false));
        PROPERTY_EDITOR_MAP.put(Float.class, new CustomNumberEditor(Float.class, true));
        PROPERTY_EDITOR_MAP.put(double.class, new CustomNumberEditor(Double.class, false));
        PROPERTY_EDITOR_MAP.put(Double.class, new CustomNumberEditor(Double.class, true));
        PROPERTY_EDITOR_MAP.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
        PROPERTY_EDITOR_MAP.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
    }

    public static <T> T toType(Class<T> tClass, String strObj) {
        if( tClass == String.class ){
            return (T)strObj;
        }
        if( !PROPERTY_EDITOR_MAP.containsKey(tClass) ){
            log.error("cannot covert the value to [{}] from value [{}]", tClass, strObj);
            return null;
        }
        PropertyEditor propertyEditor = PROPERTY_EDITOR_MAP.get(tClass);
        propertyEditor.setAsText(strObj);
        return (T)propertyEditor.getValue();
    }
}
