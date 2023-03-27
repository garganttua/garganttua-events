package com.gtech.garganttua.core.spec.objects;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gtech.garganttua.core.context.GGContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGAssetContext {
	
	private String assetId;
	
	private Date date;
	
	private String assetName;
	
	private String assetVersion;
	
	private List<GGContextObjDescriptor> processors;
	
	private List<GGContextObjDescriptor> connectors;
	
	private List<GGContextObjDescriptor> locks;
	
	private Map<String, Map<String, GGContext>> context;
	
}
