package com.webfirmframework.wffweb.internal;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ObjectIdTest {

    @Test
    public void testEquals() {
        final UUID uuid = UUID.randomUUID();
        ObjectId objectId1 = new ObjectId(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
        ObjectId objectId2 = new ObjectId(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
        assertEquals(objectId1.id(), objectId2.id());
        assertNotEquals(objectId1, objectId2);
        assertNotEquals(0, objectId1.compareTo(objectId2));
        assertEquals(-1, objectId1.compareTo(objectId2));

        ObjectId objectId3 = new ObjectId(0, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
        ObjectId objectId4 = new ObjectId(0, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
        assertEquals(objectId3.id(), objectId4.id());
        assertEquals(objectId3, objectId4);
        assertEquals(0, objectId3.compareTo(objectId4));
        assertEquals(objectId2.id(), objectId3.id());
    }
}