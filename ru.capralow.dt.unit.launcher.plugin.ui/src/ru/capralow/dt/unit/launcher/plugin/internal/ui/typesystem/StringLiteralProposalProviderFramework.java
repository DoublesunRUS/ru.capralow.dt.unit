package ru.capralow.dt.unit.launcher.plugin.internal.ui.typesystem;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.util.Triple;

import com._1c.g5.modeling.xtext.scoping.IIndexSlicePredicateService;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.ui.contentassist.stringliteral.AbstractStringLiteralProposalProvider;

public class StringLiteralProposalProviderFramework extends AbstractStringLiteralProposalProvider {

	@Override
	public List<Triple<String, String, IBslStringLiteralProposalImageProvider>> computeProposals(
			Triple<EObject, List<Expression>, Integer> context, String content, IScopeProvider scopeProvider,
			IIndexSlicePredicateService slicePredicateService, boolean isRussian) {
		return Collections.emptyList();
	}

	@Override
	public QualifiedName getExportedName(Triple<EObject, List<Expression>, Integer> context,
			IScopeProvider scopeProvider) {
		return null;
	}

	@Override
	public List<IReferenceDescription> getReferenceDescriptions(Triple<EObject, List<Expression>, Integer> context,
			IScopeProvider scopeProvider) {
		return Collections.emptyList();
	}

	@Override
	public boolean isAppropriate(Triple<EObject, List<Expression>, Integer> context) {
		return false;
	}

}
