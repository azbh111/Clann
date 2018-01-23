/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.Refection;

import java.lang.reflect.Field;

/**
 *
 * @author zyp
 */
public class RefField<T> {

        private Field field;

        /**
         * @return passed field
         */
        public Field getRealField() {
            return field;
        }

        /**
         * @return owner class of field
         */
        
        public RefClass getRefClass() {
            return new RefClass(field.getDeclaringClass());
        }

        /**
         * @return type of field
         */
        
        public RefClass<T> getFieldRefClass() {
            return new RefClass(field.getType());
        }

        public RefField(Field field) {
            this.field = field;
            field.setAccessible(true);
        }

        /**
         * apply fiend for object
         *
         * @param e applied object
         * @return RefExecutor with getter and setter
         */
        public RefExecutor of(Object e) {
            return new RefExecutor(e);
        }

        public class RefExecutor {

            private Object e;

            public RefExecutor(Object e) {
                this.e = e;
            }

            /**
             * set field value for applied object
             *
             * @param param value
             */
            public void set(T param) {
                try {
                    field.set(e, param);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * get field value for applied object
             *
             * @return value of field
             */
            
            public T get() {
                try {
                    return (T) field.get(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }