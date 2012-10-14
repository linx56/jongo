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

package org.jongo;

import org.bson.types.ObjectId;
import org.jongo.model.Friend;
import org.jongo.model.LinkedFriend;
import org.jongo.util.JongoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

public class FindByObjectIdTest extends JongoTestCase {

    private MongoCollection collection;

    @Before
    public void setUp() throws Exception {
        collection = createEmptyCollection("friends");
    }

    @After
    public void tearDown() throws Exception {
        dropCollection("friends");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNullObjectId() throws Exception {
        collection.findOne((ObjectId) null);
    }

    @Test
    public void canFindOneWithObjectId() throws Exception {
        /* given */
        Friend john = new Friend(new ObjectId(), "John");
        collection.save(john);

        Friend foundFriend = collection.findOne(john.getId()).as(Friend.class);

        /* then */
        assertThat(foundFriend).isNotNull();
        assertThat(foundFriend.getId()).isEqualTo(john.getId());
    }

    @Test
    public void canFindOneWithOid() throws Exception {
        /* given */
        ObjectId id = new ObjectId();
        Friend john = new Friend(id, "John");
        collection.save(john);

        Friend foundFriend = collection.findOne("{_id:{$oid:#}}", id.toString()).as(Friend.class);

        /* then */
        assertThat(foundFriend).isNotNull();
        assertThat(foundFriend.getId()).isEqualTo(id);
    }

    @Test
    public void canFindWithOid() throws Exception {
        /* given */
        ObjectId id = new ObjectId();
        Friend john = new Friend(id, "John");
        collection.save(john);

        Iterator<Friend> friends = collection.find("{_id:{$oid:#}}", id.toString()).as(Friend.class).iterator();

        /* then */
        assertThat(friends.hasNext()).isTrue();
        assertThat(friends.next().getId()).isEqualTo(id);
    }

    @Test
    public void canFindWithTwoOid() throws Exception {
        /* given */
        ObjectId id1 = new ObjectId();
        Friend john = new Friend(id1, "John");
        ObjectId id2 = new ObjectId();
        Friend peter = new Friend(id2, "Peter");

        collection.save(john);
        collection.save(peter);

        Iterable<Friend> friends = collection.find("{$or :[{_id:{$oid:#}},{_id:{$oid:#}}]}", id1.toString(),id2.toString()).as(Friend.class);

        /* then */
        assertThat(friends.iterator().hasNext()).isTrue();
        for (Friend friend : friends) {
            assertThat(friend.getId()).isIn(id1, id2);
        }
    }

    @Test
    public void canFindWithOidNamed() throws Exception {
        /* given */
        ObjectId id = new ObjectId();
        LinkedFriend john = new LinkedFriend(id);
        collection.save(john);

        Iterator<LinkedFriend> friends = collection.find("{friendRelationId:{$oid:#}}", id.toString()).as(LinkedFriend.class).iterator();

        /* then */
        assertThat(friends.hasNext()).isTrue();
        assertThat(friends.next().getRelationId()).isEqualTo(id);
    }
}
