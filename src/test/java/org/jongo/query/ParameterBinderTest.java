/*
 * Copyright (C) 2011 Benoit GUEROUT <bguerout at gmail dot com> and Yves AMSELLEM <amsellem dot yves at gmail dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jongo.query;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jongo.marshall.Marshaller;
import org.jongo.util.ErrorObject;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;

public class ParameterBinderTest {

    private ParameterBinder binder;
    private Marshaller marshaller;

    @Before
    public void setUp() throws Exception {
        marshaller = mock(Marshaller.class);
        binder = new ParameterBinder(marshaller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInvalidParameter() throws Exception {

        when(marshaller.marshall(anyObject())).thenThrow(new RuntimeException());

        binder.bind("{id:#}", new ErrorObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenNotEnoughParameters() throws Exception {

        binder.bind("{id:#,id2:#}", "123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenNotTooManyParameters() throws Exception {

        binder.bind("{id:#}", 123, 456);
    }

    @Test
    public void shouldBindOneParameter() throws Exception {

        String query = binder.bind("{id:#}", 123);

        assertThat(query).isEqualTo("{id:123}");
    }

    @Test
    public void shouldBindManyParameters() throws Exception {

        String query = binder.bind("{id:#, test:#}", 123, 456);

        assertThat(query).isEqualTo("{id:123, test:456}");
    }


    @Test
    public void shouldBindNonBsonPrimitiveParameters() throws Exception {

        when(marshaller.marshall(anyObject())).thenReturn(new BasicDBObject("custom", "object"));

        String query = binder.bind("{test:#}", new Object());

        assertThat(query).isEqualTo("{test:{ \"custom\" : \"object\"}}");
    }

    @Test
    public void shouldBindParameterWithCustomToken() throws Exception {

        ParameterBinder binderWithToken = new ParameterBinder(marshaller, "@");

        String query = binderWithToken.bind("{id:@}", 123);

        assertThat(query).isEqualTo("{id:123}");
    }

}

