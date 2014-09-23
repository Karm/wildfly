/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.clustering.jgroups.subsystem;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OUTCOME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUCCESS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.VALUE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.model.test.FailedOperationTransformationConfig.NewAttributesConfig;
import org.jboss.as.model.test.ModelTestControllerVersion;
import org.jboss.as.model.test.ModelTestUtils;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.subsystem.test.KernelServicesBuilder;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases for transformers used in the JGroups subsystem.
 *
 * AS version / model version / schema version overview
 * 7.1.1 / 1.1.0 / 1_1
 * 7.1.2 / 1.1.0 / 1_1
 * 7.1.3 / 1.1.0 / 1_1
 * 7.2.0 / 1.2.0 / 1_1
 * 8.0.0 / 2.0.0 / 2_0
 *
 * @author <a href="tomaz.cerar@redhat.com">Tomaz Cerar</a>
 * @author Richard Achmatowicz (c) 2011 Red Hat Inc.
 * @author Radoslav Husar
 */
@RunWith(BMUnitRunner.class)
public class TransformersTestCase extends OperationTestCaseBase {

    private static String formatSubsystemArtifact(ModelTestControllerVersion version) {
        return formatArtifact("org.wildfly:wildfly-clustering-jgroups:%s", version);
    }

    private static String formatLegacySubsystemArtifact(ModelTestControllerVersion version) {
        return formatArtifact("org.jboss.as:jboss-as-clustering-jgroups:%s", version);
    }

    private static String formatArtifact(String pattern, ModelTestControllerVersion version) {
        return String.format(pattern, version.getMavenGavVersion());
    }

    @Test
    public void testTransformerAS712() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.V7_1_2_FINAL;
        testTransformation(JGroupsModel.VERSION_1_1_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerAS713() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.V7_1_3_FINAL;
        testTransformation(JGroupsModel.VERSION_1_1_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerAS720() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.V7_2_0_FINAL;
        testTransformation(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerWF800() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.WILDFLY_8_0_0_FINAL;
        testTransformation(JGroupsModel.VERSION_2_0_0, version, formatSubsystemArtifact(version));
    }

    @Test
    public void testTransformerWF810() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.WILDFLY_8_1_0_FINAL;
        testTransformation(JGroupsModel.VERSION_2_0_0, version, formatSubsystemArtifact(version));
    }

    @Test
    public void testTransformerEAP600() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_0_0;
        testTransformation(JGroupsModel.VERSION_1_1_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerEAP601() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_0_1;
        testTransformation(JGroupsModel.VERSION_1_1_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerEAP610() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_1_0;
        testTransformation(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerEAP611() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_1_1;
        testTransformation(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerEAP620() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_2_0;
        testTransformation(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testTransformerEAP630() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_3_0;
        testTransformation(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    /**
     * Tests transformation of model from current version into specified version.
     *
     * @throws Exception
     */
    private void testTransformation(JGroupsModel model, ModelTestControllerVersion controller, String ... mavenResourceURLs) throws Exception {
        ModelVersion version = model.getVersion();
        String subsystemXml = readResource("subsystem-jgroups-transform.xml");

        // create builder for current subsystem version
        KernelServicesBuilder builder = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT)
                .setSubsystemXml(subsystemXml);

        // initialize the legacy services and add required jars
        builder.createLegacyKernelServicesBuilder(null, controller, version)
                .addMavenResourceURL(mavenResourceURLs);

        KernelServices mainServices = builder.build();
        Assert.assertTrue(mainServices.isSuccessfulBoot());
        Assert.assertTrue(mainServices.getLegacyServices(version).isSuccessfulBoot());

        // check that both versions of the legacy model are the same and valid
        checkSubsystemModelTransformation(mainServices, version);
    }

    /**
     * Tests resolution of property expressions during performRuntime()
     *
     * This test uses Byteman to inject code into AbstractAddStepHandler.performRuntime() to
     * resolve the value of an expression and check that expression resolution is working as expected.
     *
     * The test is currently broken due to an outstanding class loading problem with Byteman, but it is included
     * here for re-enabling when the issue is resolved.
     *
     * @throws Exception
     */
    @Ignore
    @Test
    @BMRule(name="Test support for expression resolution",
            targetClass="^org.jboss.as.controller.AbstractAddStepHandler",
            targetMethod="performRuntime",
            targetLocation="AT ENTRY",
            binding="context:OperationContext = $1; operation:ModelNode = $2; model:ModelNode = $3",
            condition="operation.hasDefined(\"name\") AND operation.hasDefined(\"value\")",
            action="traceln(\"resolved value = \" + org.jboss.as.clustering.jgroups.subsystem.PropertyResourceDefinition.VALUE.resolveModelAttribute(context,model))")
    public void testProtocolStackPropertyResolve() throws Exception {

        // Parse and install the XML into the controller
        String subsystemXml =  getSubsystemXml() ;
        KernelServices services = createKernelServicesBuilder(null).setSubsystemXmlResource(subsystemXml).build();

        // set a property to have an expression and let Byteman intercept the performRuntime call

        // build an ADD command to add a transport property using expression value
        ModelNode operation = getTransportPropertyAddOperation("maximal", "bundler_type", "${the_bundler_type:new}");

        // perform operation on the 1.1.1 model
        ModelNode mainResult = services.executeOperation(operation);
        assertEquals(mainResult.toJSONString(true), SUCCESS, mainResult.get(OUTCOME).asString());
    }

    @Test
    public void testRejectionsAS712() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.V7_1_2_FINAL;
        testRejections_1_1_0(version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsAS713() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.V7_1_3_FINAL;
        testRejections_1_1_0(version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsAS720() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.V7_2_0_FINAL;
        this.testRejections(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsWF800() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.WILDFLY_8_0_0_FINAL;
        this.testRejections(JGroupsModel.VERSION_2_0_0, version, formatSubsystemArtifact(version));
    }

    @Test
    public void testRejectionsWF810() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.WILDFLY_8_1_0_FINAL;
        this.testRejections(JGroupsModel.VERSION_2_0_0, version, formatSubsystemArtifact(version));
    }

    @Test
    public void testRejectionsEAP600() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_0_0;
        testRejections_1_1_0(version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsEAP601() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_0_1;
        testRejections_1_1_0(version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsEAP610() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_1_0;
        this.testRejections(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsEAP611() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_1_1;
        this.testRejections(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsEAP620() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_2_0;
        this.testRejections(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    @Test
    public void testRejectionsEAP630() throws Exception {
        ModelTestControllerVersion version = ModelTestControllerVersion.EAP_6_3_0;
        this.testRejections(JGroupsModel.VERSION_1_2_0, version, formatLegacySubsystemArtifact(version));
    }

    /**
     * Tests rejection of resources / attributes / operations in 1.1.0 model.
     *
     * @throws Exception
     */
    private void testRejections_1_1_0(ModelTestControllerVersion controllerVersion, String ... mavenResourceURLs) throws Exception {
        ModelVersion version = JGroupsModel.VERSION_1_1_0.getVersion();
        // create builder for current subsystem version
        KernelServicesBuilder builder = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT);

        // initialize the legacy services and add required jars
        builder.createLegacyKernelServicesBuilder(null, controllerVersion, version)
                .addMavenResourceURL(mavenResourceURLs)
                //TODO storing the model triggers the weirdness mentioned in SubsystemTestDelegate.LegacyKernelServiceInitializerImpl.install()
                //which is strange since it should be loading it all from the current jboss modules
                //Also this works in several other tests
                .dontPersistXml();

        KernelServices mainServices = builder.build();
        Assert.assertTrue(mainServices.isSuccessfulBoot());
        KernelServices legacyServices = mainServices.getLegacyServices(version);
        Assert.assertNotNull(legacyServices);
        Assert.assertTrue(legacyServices.isSuccessfulBoot());

        //Use the real xml with expressions for testing all the attributes
        PathAddress subsystemAddress = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, JGroupsExtension.SUBSYSTEM_NAME));
        ModelTestUtils.checkFailedTransformedBootOperations(
                mainServices,
                version,
                builder.parseXmlResource(JGroupsSchema.CURRENT.format("subsystem-jgroups-%d_%d.xml")),
                new FailedOperationTransformationConfig()
                        // expect certain rejected expressions
                        .addFailedAttribute(
                                subsystemAddress.append(StackResourceDefinition.WILDCARD_PATH)
                                        .append(TransportResourceDefinition.PATH),
                                new FailedOperationTransformationConfig.RejectExpressionsConfig(ModelKeys.SHARED))
                        .addFailedAttribute(
                                subsystemAddress.append(StackResourceDefinition.WILDCARD_PATH)
                                        .append(TransportResourceDefinition.PATH)
                                        .append(PropertyResourceDefinition.WILDCARD_PATH),
                                new FailedOperationTransformationConfig.RejectExpressionsConfig(VALUE))
                        .addFailedAttribute(
                                subsystemAddress.append(StackResourceDefinition.WILDCARD_PATH)
                                        .append(ProtocolResourceDefinition.WILDCARD_PATH)
                                        .append(PropertyResourceDefinition.WILDCARD_PATH),
                                new FailedOperationTransformationConfig.RejectExpressionsConfig(VALUE))
                        // expect rejection of relay and child
                        .addFailedAttribute(
                                subsystemAddress.append(StackResourceDefinition.WILDCARD_PATH).append(RelayResourceDefinition.PATH),
                                FailedOperationTransformationConfig.REJECTED_RESOURCE)
                        .addFailedAttribute(
                                subsystemAddress.append(StackResourceDefinition.WILDCARD_PATH).append(RelayResourceDefinition.PATH).append(RemoteSiteResourceDefinition.WILDCARD_PATH),
                                FailedOperationTransformationConfig.REJECTED_RESOURCE)
        );
    }

    private void testRejections(JGroupsModel model, ModelTestControllerVersion controller, String... dependencies) throws Exception {
        ModelVersion version = model.getVersion();

        // create builder for current subsystem version
        KernelServicesBuilder builder = createKernelServicesBuilder(AdditionalInitialization.MANAGEMENT);

        // initialize the legacy services and add required jars
        builder.createLegacyKernelServicesBuilder(null, controller, version)
                .addMavenResourceURL(dependencies)
                .dontPersistXml();

        KernelServices services = builder.build();
        Assert.assertTrue(services.isSuccessfulBoot());
        KernelServices legacyServices = services.getLegacyServices(version);
        Assert.assertNotNull(legacyServices);
        Assert.assertTrue(legacyServices.isSuccessfulBoot());

        List<ModelNode> operations = builder.parseXmlResource("subsystem-jgroups-transform-reject.xml");
        ModelTestUtils.checkFailedTransformedBootOperations(services, version, operations, createFailedOperationTransformationConfig(version));
    }

    private static FailedOperationTransformationConfig createFailedOperationTransformationConfig(ModelVersion version) {
        FailedOperationTransformationConfig config = new FailedOperationTransformationConfig();

        PathAddress subsystemAddress = PathAddress.pathAddress(JGroupsSubsystemResourceDefinition.PATH);

        if (JGroupsModel.VERSION_3_0_0.requiresTransformation(version)) {
            config.addFailedAttribute(subsystemAddress, new NewAttributesConfig(JGroupsSubsystemResourceDefinition.DEFAULT_CHANNEL));
            config.addFailedAttribute(subsystemAddress.append(ChannelResourceDefinition.WILDCARD_PATH), FailedOperationTransformationConfig.REJECTED_RESOURCE);
        }

        if (JGroupsModel.VERSION_2_0_0.requiresTransformation(version)) {
            PathAddress stackAddress = subsystemAddress.append(StackResourceDefinition.WILDCARD_PATH);
            PathAddress relayAddress = stackAddress.append(RelayResourceDefinition.PATH);
            config.addFailedAttribute(relayAddress, FailedOperationTransformationConfig.REJECTED_RESOURCE);
            config.addFailedAttribute(relayAddress.append(RemoteSiteResourceDefinition.WILDCARD_PATH), FailedOperationTransformationConfig.REJECTED_RESOURCE);
        }

        return config;
    }
}
