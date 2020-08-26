package com.luckydraw.dao;

import java.util.Map;

public interface FileAccess
{
    void writeToExcel( String title, Map<Integer, String> data, int column );

    void deleteOutputFile();
}
