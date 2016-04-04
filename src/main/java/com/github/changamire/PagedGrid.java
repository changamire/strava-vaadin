package com.github.changamire;

import com.vaadin.data.Container;
import com.vaadin.ui.Grid;

public class PagedGrid extends Grid {

    public PagedGrid(final Container.Indexed dataSource) {
        super(null, dataSource);
    }
}
