package de.linogistix.los.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.query.dto.SequenceNumberTO;
import de.wms2.mywms.sequence.SequenceNumber;

/**
 * @author krane
 *
 */
@Stateless
public class SequenceNumberQueryBean extends BusinessObjectQueryBean<SequenceNumber>
		implements SequenceNumberQueryRemote {

	public String getUniqueNameProp() {
		return "name";
	}

	static List<BODTOConstructorProperty> BODTOConstructorProperties = new ArrayList<BODTOConstructorProperty>();

	static {
		BODTOConstructorProperties.add(new BODTOConstructorProperty("id", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("version", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("name", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("format", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("counter", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("startCounter", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("endCounter", false));
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		return BODTOConstructorProperties;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getBODTOClass() {
		return SequenceNumberTO.class;
	}

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

		Long number = null;
		try {
			number = Long.parseLong(value);
		} catch (Throwable t) {
		}

		TemplateQueryWhereToken token;
		if (number != null) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "id", number);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "counter", number);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "startCounter", number);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "endCounter", number);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "format", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		return ret;
	}
}
