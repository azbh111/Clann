/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.Refection;

import java.lang.reflect.Constructor;

/**
 *
 * @author zyp
 */
public class RefConstructor<C> {

        private final Constructor<C> constructor;

        /**
         * @return passed constructor
         */
        public Constructor<C> getRealConstructor() {
            return constructor;
        }

        /**
         * @return owner class of method
         */
        public RefClass<C> getRefClass() {
            return new RefClass<C>(constructor.getDeclaringClass());
        }

        public RefConstructor(Constructor<C> constructor) {
            this.constructor = constructor;
            constructor.setAccessible(true);
        }

        /**
         * create new instance with constructor
         *
         * @param params parameters for constructor
         * @return new object
         * @throws RuntimeException if something went wrong
         */
        public C create(Object... params) {
            try {
                return constructor.newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }