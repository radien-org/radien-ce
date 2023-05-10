package io.radien.ms.doctypemanagement.resource;


import io.radien.api.entity.Page;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.service.MixinDefinitionService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.ws.rs.core.Response;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

public class MixinDefinitionResourceTest  {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private MixinDefinitionResource resource;

    @Mock
    private MixinDefinitionService service;

    @Test
    public void testGetAll() {
        when(service.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenReturn(new Page<>());
        Response result = resource.getAll("",1,10,new ArrayList<>(),false);
        assertEquals(200,result.getStatusInfo().getStatusCode());
        assertNotNull(result.getEntity());
    }

    @Test
    public void testGetById() {
        when(service.getById(anyLong())).thenReturn(new MixinDefinitionDTO());
        Response result = resource.getById(1L);
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNotNull(result.getEntity());
    }

    @Test
    public void testDelete() {
        Response result = resource.delete(1L);
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNull(result.getEntity());
    }

    @Test
    public void testSave() {
        Response result = resource.save(new MixinDefinitionDTO());
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNull(result.getEntity());
    }

    @Test
    public void testGetTotalRecordsCount() {
        when(service.getTotalRecordsCount())
                .thenReturn(10L);
        Response result = resource.getTotalRecordsCount();
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNotNull(result.getEntity());
    }
}