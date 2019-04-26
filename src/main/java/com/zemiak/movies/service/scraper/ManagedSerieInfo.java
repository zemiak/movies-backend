package com.zemiak.movies.service.scraper;

import java.util.*;

public class ManagedSerieInfo {
    private Integer id;
    private String name;
    private ResourceBundle descriptions;
    private Set<Integer> range;

    public ManagedSerieInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceBundle getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(ResourceBundle descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ManagedSerieInfo other = (ManagedSerieInfo) obj;
        return Objects.equals(this.id, other.getId());
    }

    public Set<Integer> getRange() {
        return range;
    }

    public void setRange(Set<Integer> range) {
        this.range = new HashSet(range);
    }
}
