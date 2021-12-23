package io.radien.ms.ecm.resource;

import io.radien.api.model.legal.SystemLegalDocumentTypeSearchFilter;
import io.radien.api.service.legal.LegalDocumentTypeServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.LegalDocumentTypeNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentTypeSearchFilter;
import io.radien.ms.ecm.client.services.LegalDocumentTypeResourceClient;
import io.radien.ms.legal.entities.LegalDocumentTypeEntity;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("legalDocType")
@RequestScoped
public class LegalDocumentTypeResource implements LegalDocumentTypeResourceClient {

    @Inject
    private LegalDocumentTypeServiceAccess legalDocumentTypeServiceAccess;

    private static final Logger log = LoggerFactory.getLogger(LegalDocumentTypeResource.class);

    @Override
    public Response getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        log.info("Retrieving Legal document types using pagination. Search {} Page No {}. Page Size {} Sort by {} Asc{}.",
                search, pageNo, pageSize, sortBy, isAscending);
        try {
            return Response.ok(legalDocumentTypeServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
        }
        catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response getLegalDocumentTypes(String name, Long tenantId, Boolean toBeShown, Boolean toBeAccepted,
                                          List<Long> ids, boolean isExact, boolean isLogicalConjunction) {
        log.info("Retrieving Legal document types. Name {} tenant {} shown {} accepted {} ids {} exact{} logical{}.",
                name, tenantId, toBeShown, toBeAccepted, ids, isExact, isLogicalConjunction);
        try {
            SystemLegalDocumentTypeSearchFilter filter = new LegalDocumentTypeSearchFilter(name, tenantId,
                    toBeShown, toBeAccepted, ids, isExact, isLogicalConjunction);
            return Response.ok(legalDocumentTypeServiceAccess.getLegalDocumentTypes(filter)).build();
        }
        catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response getById(Long id) {
        log.info("Retrieving By Id {}.", id);
        try {
            return Response.ok(legalDocumentTypeServiceAccess.get(id)).build();
        } catch (LegalDocumentTypeNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response delete(long id) {
        log.info("Deleting LegalDocumentType for id {}", id);
        try {
            legalDocumentTypeServiceAccess.delete(id);
            return Response.ok().build();
        } catch (LegalDocumentTypeNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response create(LegalDocumentType legalDocumentType) {
        log.info("Creating Legal Document Type");
        try {
            legalDocumentTypeServiceAccess.create(new LegalDocumentTypeEntity(legalDocumentType));
            return Response.ok().build();
        } catch (UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response update(long id, LegalDocumentType legalDocumentType) {
        log.info("Updating Legal Document Type, id {}", id);
        try {
            legalDocumentType.setId(id);
            legalDocumentTypeServiceAccess.update(new LegalDocumentTypeEntity(legalDocumentType));
            return Response.ok().build();
        } catch (UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (LegalDocumentTypeNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }
}
