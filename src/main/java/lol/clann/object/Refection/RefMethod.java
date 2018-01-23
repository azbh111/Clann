/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.Refection;

import java.lang.reflect.Method;

/**
 *
 * @author zyp
 */
public class RefMethod<Z> {

        private final Method method;
        private final int argumentsCount;

        /**
         * @return passed method
         */
        public Method getRealMethod() {
            return method;
        }

        /**
         * @return owner class of method
         */
        
        public RefClass getRefClass() {
            return new RefClass(method.getDeclaringClass());
        }

        /**
         * @return class of method return type
         */
        
        public RefClass<Z> getReturnRefClass() {
            return new RefClass(method.getReturnType());
        }

        public RefMethod(Method method) {
            this.method = method;
            this.argumentsCount = method.getParameterTypes().length;
            method.setAccessible(true);
        }

        public int getArgumentsCount() {
            return argumentsCount;
        }

        /**
         * apply method to object
         *
         * @param e object to which the method is applied
         * @return RefExecutor with method call(params[])
         */
        public RefExecutor of(Object e) {
            return new RefExecutor(e);
        }

        /**
         * call static method
         *
         * @param params sent parameters
         * @return return value
         */
        
        public Z call(Object... params) {
            try {
                return (Z) method.invoke(null, params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public class RefExecutor {

            Object e;

            public RefExecutor(Object e) {
                this.e = e;
            }

            /**
             * apply method for selected object
             *
             * @param params sent parameters
             * @return return value
             * @throws RuntimeException if something went wrong
             */
            
            public Z call(Object... params) {
                try {
                    return (Z) method.invoke(e, params);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }