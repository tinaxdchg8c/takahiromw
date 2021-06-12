/**
 * Copyright (c) 2012-2013, Michael Yang ��� (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tsz.afinal.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.db.table.ManyToOne;
import net.tsz.afinal.db.table.OneToMany;
import net.tsz.afinal.db.table.Property;
import net.tsz.afinal.exception.DbException;

public class ClassUtils {
	
	
	/**
	 * ����ʵ���� ��� ʵ�����Ӧ�ı���
	 * @param entity
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if(table == null || table.name().trim().length() == 0 ){
			//��û��ע���ʱ��Ĭ�������������Ϊ����,���ѵ㣨.���滻Ϊ�»���(_)
			return clazz.getName().replace('.', '_');
		}
		return table.name();
	}
	
	public static Object getPrimaryKeyValue(Object entity) {
		return FieldUtils.getFieldValue(entity,ClassUtils.getPrimaryKeyField(entity.getClass()));
	}
	
	/**
	 * ����ʵ���� ��� ʵ�����Ӧ�ı���
	 * @param entity
	 * @return
	 */
	public static String getPrimaryKeyColumn(Class<?> clazz) {
		String primaryKey = null ;
		Field[] fields = clazz.getDeclaredFields();
		if(fields != null){
			Id idAnnotation = null ;
			Field idField = null ;
			
			for(Field field : fields){ //��ȡIDע��
				idAnnotation = field.getAnnotation(Id.class);
				if(idAnnotation != null){
					idField = field;
					break;
				}
			}
			
			if(idAnnotation != null){ //��IDע��
				primaryKey = idAnnotation.column();
				if(primaryKey == null || primaryKey.trim().length() == 0)
					primaryKey = idField.getName();
			}else{ //û��IDע��,Ĭ��ȥ�� _id �� id Ϊ����������Ѱ�� _id
				for(Field field : fields){
					if("_id".equals(field.getName()))
						return "_id";
				}
				
				for(Field field : fields){
					if("id".equals(field.getName()))
						return "id";
				}
			}
		}else{
			throw new RuntimeException("this model["+clazz+"] has no field");
		}
		return primaryKey;
	}
	
	
	/**
	 * ����ʵ���� ��� ʵ�����Ӧ�ı���
	 * @param entity
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<?> clazz) {
		Field primaryKeyField = null ;
		Field[] fields = clazz.getDeclaredFields();
		if(fields != null){
			
			for(Field field : fields){ //��ȡIDע��
				if(field.getAnnotation(Id.class) != null){
					primaryKeyField = field;
					break;
				}
			}
			
			if(primaryKeyField == null){ //û��IDע��
				for(Field field : fields){
					if("_id".equals(field.getName())){
						primaryKeyField = field;
						break;
					}
				}
				if(primaryKeyField == null){ // ���û��_id���ֶ�
					for(Field field : fields){
						if("id".equals(field.getName())){
							primaryKeyField = field;
							break;
						}
					}
				}
			}
		}else{
			throw new RuntimeException("this model["+clazz+"] has no field");
		}
		return primaryKeyField;
	}
	
	
	/**
	 * ����ʵ���� ��� ʵ�����Ӧ�ı���
	 * @param entity
	 * @return
	 */
	public static String getPrimaryKeyFieldName(Class<?> clazz) {
		Field f = getPrimaryKeyField(clazz);
		return f==null ? null:f.getName();
	}
	
	
	
	/**
	 * ������ת��ΪContentValues
	 * 
	 * @param entity
	 * @param selective �Ƿ���� ֵΪnull���ֶ�
	 * @return
	 */
	public static List<Property> getPropertyList(Class<?> clazz) {
		
		List<Property> plist = new ArrayList<Property>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
			for (Field f : fs) {
				//�����ǻ����������ͺ�û�б�˲ʱ̬���ֶ�
				if (!FieldUtils.isTransient(f) && FieldUtils.isBaseDateType(f)) {
					
					if(f.getName().equals(primaryKeyFieldName)) //��������
						continue;
					
					Property property = new Property();
				
					property.setColumn(FieldUtils.getColumnByField(f));
					property.setFieldName(f.getName());
					property.setDataType(f.getType());
					property.setDefaultValue(FieldUtils.getPropertyDefaultValue(f));
					property.setSet(FieldUtils.getFieldSetMethod(clazz, f));
					property.setGet(FieldUtils.getFieldGetMethod(clazz, f));
					
					plist.add(property);
				}
			}
			return plist;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * ������ת��ΪContentValues
	 * 
	 * @param entity
	 * @param selective �Ƿ���� ֵΪnull���ֶ�
	 * @return
	 */
	public static List<ManyToOne> getManyToOneList(Class<?> clazz) {
		
		List<ManyToOne> mList = new ArrayList<ManyToOne>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				if (!FieldUtils.isTransient(f) && FieldUtils.isManyToOne(f)) {
					
					ManyToOne mto = new ManyToOne();
					mto.setManyClass(f.getType());
					mto.setColumn(FieldUtils.getColumnByField(f));
					mto.setFieldName(f.getName());
					mto.setDataType(f.getType());	
					mto.setSet(FieldUtils.getFieldSetMethod(clazz, f));
					mto.setGet(FieldUtils.getFieldGetMethod(clazz, f));
					
					mList.add(mto);
				}
			}
			return mList;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * ������ת��ΪContentValues
	 * 
	 * @param entity
	 * @param selective �Ƿ���� ֵΪnull���ֶ�
	 * @return
	 */
	public static List<OneToMany> getOneToManyList(Class<?> clazz) {
		
		List<OneToMany> oList = new ArrayList<OneToMany>();
		try {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				if (!FieldUtils.isTransient(f) && FieldUtils.isOneToMany(f)) {
					
					OneToMany otm = new OneToMany();
					
					otm.setColumn(FieldUtils.getColumnByField(f));
					otm.setFieldName(f.getName());
					
					Type type = f.getGenericType();
					
					if(type instanceof ParameterizedType){
						ParameterizedType pType = (ParameterizedType) f.getGenericType();
						Class<?> pClazz = (Class<?>)pType.getActualTypeArguments()[0];
						if(pClazz!=null)
							otm.setOneClass(pClazz);
					}else{
						throw new DbException("getOneToManyList Exception:"+f.getName()+"'s type is null");
					}
					
					otm.setDataType(f.getClass());
					otm.setSet(FieldUtils.getFieldSetMethod(clazz, f));
					otm.setGet(FieldUtils.getFieldGetMethod(clazz, f));
					
					oList.add(otm);
				}
			}
			return oList;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}	
	
	
}
