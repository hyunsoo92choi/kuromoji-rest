package com.eBayJP.kuromoji.app.analytics.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eBayJP.kuromoji.app.analytics.entity.AnalyticsTokenEntity;
import com.eBayJP.kuromoji.app.analytics.service.KuromojiAnalyticsService;
import com.eBayJP.kuromoji.common.entity.request.KuromojiRequestEntity;
import com.eBayJP.kuromoji.common.version.ApiVersion;

/**
 * <pre>
 * com.eBayJP.kuromoji.app.analytics.controller_KuromojiAnalyticsController.java
 * </pre>
 * @date : 2019. 6. 28.
 * @author : hychoi
 */
@RestController
@RequestMapping(value = "api/{version}/analytics")
public class KuromojiAnalyticsController {
	
	private final Logger log = LoggerFactory.getLogger(KuromojiAnalyticsController.class);
	
	@Autowired
	private KuromojiAnalyticsService kuromojiAnalyticsService;
	
	/**
	 * <pre>
	 * 1. 개요 : 
	 * 2. 처리내용 : 
	 * </pre>
	 * @Method Name : tokenize
	 * @date : 2019. 6. 28.
	 * @author : hychoi
	 * @history : 
	 *	-----------------------------------------------------------------------
	 *	변경일				작성자						변경내용  
	 *	----------- ------------------- ---------------------------------------
	 *	2019. 6. 28.		hychoi				최초 작성 
	 *	2019. 7. 31.		hychoi				사용자사전 @GetMapping 개선
	 *	-----------------------------------------------------------------------
	 * 
	 * @param text
	 * @return
	 */ 
	@ApiVersion(1)
	@GetMapping(value = "/tokenize")
    public ResponseEntity<Map<String, Object>> tokenize(@RequestParam("text") String text) {
        
		log.info("[KuromojiAnalyticsController]: >>>> @GetMapping :text: {}", text);
		
//		String encodedParam = URLEncoder.encode(text, "UTF-8");
		
		Map<String, Object> model = new HashMap<String, Object>();
		List<AnalyticsTokenEntity> EntityList = new ArrayList<AnalyticsTokenEntity>();
		
		Map<String, List<AnalyticsTokenEntity>> inlineModel = kuromojiAnalyticsService.tokenizeUserDicAll(text);
		
		EntityList.addAll(inlineModel.get("tokens"));
		EntityList.addAll(inlineModel.get("otherTokens"));
		
		model.put("result",EntityList);
		
		return new ResponseEntity< Map<String, Object> >(model, HttpStatus.OK);
	}
	
	/**
	 * <pre>
	 * 1. 개요 : Post 방식으로 Text를 받아 Tokenized 된 결과를 return
	 * 2. 처리내용 : Post 방식으로 Texts 를 받아 Tokenized 된 결과를 model 에 담고 모델을 return
	 * </pre>
	 * @Method Name : tokenizeByPost
	 * @date : 2019. 6. 28.
	 * @author : hychoi
	 * @history : 
	 *	-----------------------------------------------------------------------
	 *	변경일				작성자						변경내용  
	 *	----------- ------------------- ---------------------------------------
	 *	2019. 6. 28.		hychoi				최초 작성 
	 *	-----------------------------------------------------------------------
	 * 
	 * @param requestKuromojiEntity
	 * @return
	 */ 
	@ApiVersion(1)
	@PostMapping(value = "/tokenize")
	public ResponseEntity<Map<String, Object>> tokenizeByPost(@RequestBody KuromojiRequestEntity requestKuromojiEntity) {
		
		
		List<String> textList = requestKuromojiEntity.getTexts();
		Map<String, Object> model = new HashMap<String, Object>();
		List<AnalyticsTokenEntity> EntityList = new ArrayList<AnalyticsTokenEntity>();
		
		Long startTime = System.currentTimeMillis();
		
		textList.forEach(text -> {
			log.info("[KuromojiAnalyticsController]: >>>> @@PostMapping :text: {}", text);
			StringBuilder sb = new StringBuilder(text.length());
			Map<String, List<AnalyticsTokenEntity>> inlineModel = new HashMap<String, List<AnalyticsTokenEntity>>();
			sb.append(text);
			inlineModel = kuromojiAnalyticsService.tokenizeUserDicAll(sb.toString());
			
			log.info("[KuromojiAnalyticsController]: >>>> result: {}", inlineModel);
			
			EntityList.addAll(inlineModel.get("tokens"));
			EntityList.addAll(inlineModel.get("otherTokens"));
			
	    });
		
		model.put("result",EntityList);
//		Long endTime = System.currentTimeMillis();
//		Long processTime = endTime - startTime;
//		model.put("processTime",processTime.toString());
		return new ResponseEntity< Map<String, Object> >(model, HttpStatus.OK);
	}
	
	@ApiVersion(1)
	@PostMapping(value = "/brandDic/tokenize")
	public ResponseEntity<Map<String, Object>> tokenizeByBrandDic(@RequestBody KuromojiRequestEntity requestKuromojiEntity) {
		
		List<String> textList = requestKuromojiEntity.getTexts();
		
		StringBuilder sb = new StringBuilder(textList.size());
		
		textList.forEach(text -> {
	        sb.append(text);
	        sb.append(System.lineSeparator());
	    });
		
		log.info("[KuromojiAnalyticsController]: >>>> @@tokenizeByBrandDic :text: {}", sb.toString());
		
		Map<String, Object> model = kuromojiAnalyticsService.tokenizeBrandDic(sb.toString());
		
		return new ResponseEntity< Map<String, Object> >(model, HttpStatus.OK);
	}
	
}
