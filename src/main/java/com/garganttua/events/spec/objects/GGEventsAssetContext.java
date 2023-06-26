package com.garganttua.events.spec.objects;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.garganttua.events.context.GGEventsContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsAssetContext {
	
	private String assetId;
	
	private Date date;
	
	private String assetName;
	
	private String assetVersion;
	
	private List<GGEventsContextObjDescriptor> processors;
	
	private List<GGEventsContextObjDescriptor> connectors;
	
	private List<GGEventsContextObjDescriptor> locks;
	
	private Map<String, Map<String, GGEventsContext>> context;
	
}
