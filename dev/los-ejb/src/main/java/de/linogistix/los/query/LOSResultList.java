/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class LOSResultList<E> implements List<E>, Serializable {

	private static final long serialVersionUID = 1L;

	private long resultSetSize;
	
	private long startResultIndex;
	
	private List<E> wrappedList;

	private Map<String, Object> columnSums;
		
	public LOSResultList() {
		super();
		this.wrappedList = new ArrayList<E>();
	}

	public LOSResultList(List<E> wrappedList) {
		super();
		this.wrappedList = wrappedList;
	}

	public boolean add(E o) {
		return wrappedList.add(o);
	}

	public void add(int index, E element) {
		wrappedList.add(index, element);		
	}

	public boolean addAll(Collection<? extends E> c) {
		return wrappedList.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return wrappedList.addAll(index, c);
	}

	public void clear() {
		wrappedList.clear();
	}

	public boolean contains(Object o) {
		return wrappedList.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return wrappedList.containsAll(c);
	}

	public E get(int index) {
		return wrappedList.get(index);
	}

	public int indexOf(Object o) {
		return wrappedList.indexOf(o);
	}

	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

	public Iterator<E> iterator() {
		return wrappedList.iterator();
	}

	public int lastIndexOf(Object o) {
		return wrappedList.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return wrappedList.listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return wrappedList.listIterator(index);
	}

	public boolean remove(Object o) {
		return wrappedList.remove(o);
	}

	public E remove(int index) {
		return wrappedList.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		return wrappedList.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return wrappedList.retainAll(c);
	}

	public E set(int index, E element) {
		return wrappedList.set(index, element);
	}

	public int size() {
		return wrappedList.size();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return wrappedList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return wrappedList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return wrappedList.toArray(a);
	}

	public long getResultSetSize() {
		return resultSetSize;
	}

	public void setResultSetSize(long resultSetSize) {
		this.resultSetSize = resultSetSize;
	}

	public long getStartResultIndex() {
		return startResultIndex;
	}

	public void setStartResultIndex(long startResultIndex) {
		this.startResultIndex = startResultIndex;
	}

	public void setColumnSums(Map<String, Object> m) {
		this.columnSums = m;
	}
	
	public Map<String, Object> getColumsSums(){
		return this.columnSums;
	}

}
