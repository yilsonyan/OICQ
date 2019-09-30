package com.qq.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * 自定义的源List
 * 其内容是一个List，外部通过setSources(List sources)来设置源
 * BaseList Demo 核心类
 * @ClassName ListSource
 * @author Jet
 * @date 2012-8-7
 */
public class ListSource extends BaseModel{
	private Comparator<Object> comparator=null;
	private List<Object> sources=new ArrayList<Object>();
	//通过list设置数据源
	public void setSources(List<Object> sources) {
		this.sources = sources;
	}
	//添加一个单元
	public void addCell(Object obj){
		sources.add(obj);
		notifySourceRefreshEvent(sources);
	}
	//根据索引删除一个单元
	public void removeCell(int index){
		sources.remove(index);
		notifySourceRefreshEvent(sources);
	}
	//根据值删除一个单元
	public void removeCell(Object value){
		sources.remove(value);
		notifySourceRefreshEvent(sources);
	}
	//设置一个单元
	public void setCell(int index,Object obj){
		sources.set(index, obj);
		notifySourceRefreshEvent(sources);
	}
	//获取一个单元的信息
	public Object getCell(int index){
		return sources.get(index);
	}
	//获取所有单元信息
	public List<Object> getAllCell(){
		return sources;
	}
	//移除所有
	public void removeAll(){
		sources.clear();
		notifySourceRefreshEvent(sources);
	}
	public void setSort(Comparator<Object> comparator){
		this.comparator=comparator;
		notifySourceRefreshEvent(sources);
	}
	public Comparator<Object> getComparator() {
		return comparator;
	}
}
