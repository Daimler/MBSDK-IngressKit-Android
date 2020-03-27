package com.daimler.mbingresskit;

import com.daimler.mbingresskit.common.StatusCode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatusCodeTest {
    @Test
    public void unspecifiedErrorStatusCode() throws Exception {
        assertEquals(StatusCode.UNSPECIFIED_ERROR, StatusCode.Companion.byValue(9811));
    }

    @Test
    public void unknownStatusCode() throws Exception {
        assertEquals(StatusCode.UNKNOWN, StatusCode.Companion.byValue(4711));
    }

    @Test
    public void matchedStatusCode() throws Exception {
        assertEquals(StatusCode.USER_LOGOUT, StatusCode.Companion.byValue(2001));
    }
}
