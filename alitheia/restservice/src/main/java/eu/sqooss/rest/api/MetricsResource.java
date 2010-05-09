/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package eu.sqooss.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;

/**
 *  @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
@Path("/api")
public class MetricsResource {

	public MetricsResource() {}

	@GET
    @Produces({"application/xml", "application/json"})
	@Path("/metrics/")
	public List<Metric> getMetrics() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from Metric";
		List<Metric> sp = (List<Metric>) db.doHQL(q);
		return sp;
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("/metrics/types")
	public List<MetricType> getMetricTypes() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from MetricType";
		List<MetricType> sp = (List<MetricType>) db.doHQL(q);
		return sp;
	}

	@Path("/metrics/by-id/{id}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Metric getMetricById(@PathParam("id") Long id) {
		return DAObject.loadDAObyId(id, Metric.class);
	}
	
	@Path("/metrics/by-id/{id}/{rid: .+}")
    @GET
    @Produces({"application/xml", "application/json"})
    public Metric getMetricResult(@PathParam("id") Long id,
            @PathParam("rid") String resourceIds) {
	    
	    List<Long>  ids = new ArrayList<Long>();
	    int count = 0;
	    for (String resourceId : resourceIds.split(",")) {
	        try {
	            Long l = Long.parseLong(resourceId);
	            ids.add(l);
	        } catch (NumberFormatException nfe) {}
	        
	        count++;
	        if (count >=64) {
	            break;
	        }
	    }
	    
	    Metric m = DAObject.loadDAObyId(id, Metric.class);
	    AlitheiaPlugin ap = AlitheiaCore.getInstance().getPluginAdmin().getImplementingPlugin(m.getMnemonic());
	    Class<? extends DAObject> clazz = m.getMetricType().toActivator();
	    
	    
        return null; 
    }
    
	
	@Path("/metrics/by-mnem/{mnem}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Metric getMetricByMnem(@PathParam("mnem") String name) {
		return Metric.getMetricByMnemonic(name);
	}
	
	@Path("/metrics/by-type/{type}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Set<Metric> getMetricByType(@PathParam("type") String type) {
		MetricType mt = MetricType.getMetricType(Type.fromString(type));
		return mt.getMetrics();
	}
}
