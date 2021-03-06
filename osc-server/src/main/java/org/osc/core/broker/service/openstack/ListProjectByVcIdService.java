/*******************************************************************************
 * Copyright (c) Intel Corporation
 * Copyright (c) 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.osc.core.broker.service.openstack;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.osc.core.broker.model.entities.virtualization.VirtualizationConnector;
import org.osc.core.broker.rest.client.openstack.openstack4j.Endpoint;
import org.osc.core.broker.rest.client.openstack.openstack4j.Openstack4jKeystone;
import org.osc.core.broker.service.ServiceDispatcher;
import org.osc.core.broker.service.api.ListProjectByVcIdServiceApi;
import org.osc.core.broker.service.dto.openstack.OsProjectDto;
import org.osc.core.broker.service.persistence.OSCEntityManager;
import org.osc.core.broker.service.request.BaseIdRequest;
import org.osc.core.broker.service.response.ListResponse;
import org.osgi.service.component.annotations.Component;

@Component
public class ListProjectByVcIdService extends ServiceDispatcher<BaseIdRequest, ListResponse<OsProjectDto>>
        implements ListProjectByVcIdServiceApi {

    @Override
    public ListResponse<OsProjectDto> exec(BaseIdRequest request, EntityManager em) throws Exception {
        // Initializing Entity Manager
        OSCEntityManager<VirtualizationConnector> emgr = new OSCEntityManager<>(VirtualizationConnector.class, em, this.txBroadcastUtil);

        // to do mapping
        VirtualizationConnector vc = emgr.findByPrimaryKey(request.getId());

        ListResponse<OsProjectDto> listResponse = new ListResponse<>();
        try (Openstack4jKeystone keystoneApi = new Openstack4jKeystone(new Endpoint(vc))) {
            List<OsProjectDto> projectDtoList = keystoneApi.listProjects().stream()
                    .map(project -> new OsProjectDto(project.getName(), project.getId())).collect(Collectors.toList());
            listResponse.setList(projectDtoList);
        }

        return listResponse;
    }
}
