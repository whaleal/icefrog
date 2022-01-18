package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.collection.ListUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 按大陆划分国家
 *
 * @author wh
 * @since 1.2
 */
public enum CountrySet implements Iterable< Country > {
    EURO_COUNTRIES(new Country[]{Country.AUSTRIA, Country.BELGIUM, Country.CYPRUS, Country.ESTONIA, Country.FINLAND, Country.FRANCE, Country.GERMANY, Country.GREECE, Country.IRELAND, Country.ITALY, Country.LATVIA, Country.LITHUANIA, Country.LUXEMBOURG, Country.MALTA, Country.NETHERLANDS, Country.PORTUGAL, Country.SLOVAKIA, Country.SLOVENIA, Country.SPAIN}),
    VAT_EURO_SUPPLIER(new Country[]{Country.BULGARIA, Country.CROATIA, Country.CZECH_REPUBLIC, Country.DENMARK, Country.POLAND, Country.ROMANIA, Country.SWEDEN}),
    VAT_EURO_COUNTRIES((List)Stream.concat(VAT_EURO_SUPPLIER.getCountries().stream(), EURO_COUNTRIES.getCountries().stream()).collect(Collectors.toList()));

    private final Set< Country > _countries;

    private CountrySet( Country... pCountries) {
        this(ListUtil.of(pCountries));
    }

    private CountrySet(List< Country > pCountries) {
        this._countries = new HashSet();
        this._countries.addAll(pCountries);
    }

    public Iterator< Country > iterator() {
        return this._countries.iterator();
    }

    public boolean contains( Country pCountry) {
        return this._countries.contains(pCountry);
    }

    public Set< Country > getCountries() {
        return this._countries;
    }
}
