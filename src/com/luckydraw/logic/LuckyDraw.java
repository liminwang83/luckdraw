package com.luckydraw.logic;

import java.util.Map;
import java.util.Set;

public interface LuckyDraw
{
    void saveClass4( int suffixNum1, int suffixNum2 );

    void saveClass3( int suffixNum );

    void saveClass2( Set<Integer> results );

    void saveClass1( Set<Integer> results );

    void saveSpecialAward( Set<Integer> results );

    void saveConsolationPrize( Set<Integer> results );

    void saveReplenishPrize( int level, Set<Integer> results );

    void reset();

    void cacheData();

    Map<Integer, String> randomSelectFromEmployees( int count );

    void generateExcel();
}
