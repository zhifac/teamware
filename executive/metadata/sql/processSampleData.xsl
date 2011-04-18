<?xml version="1.0" encoding="UTF-8" ?>
<!--
XSL stylesheet to turn human-readable sample data file into dbunit "flat"
format for import into the teamware database.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tw="http://gate.ac.uk/ns/teamware/sample-data"
                xmlns:exslt="http://exslt.org/common"
                exclude-result-prefixes="tw exslt"
                version="1.0">

  <xsl:output method="xml" indent="yes" />

  <!-- Parameter pointing to the installation-specific data file, which will be
       merged with the main shared one -->
  <xsl:param name="installationDataFile" select="dummy-inst-data.xml" />
  <xsl:variable name="installationData"
                select="document($installationDataFile)" />

  <!-- A bit of gymnastics to generate IDs for each of the data items -->
  <xsl:variable name="all" select="/dataset/* | $installationData/dataset/*" />

  <!-- This monster sets the allElementsRTF variable to a result tree fragment
       where each non-jforum element has a generated id attribute added to it
       -->
  <xsl:variable name="allElementsRTF">
    <xsl:for-each select="$all">
      <xsl:variable name="cur" select="." />
      <xsl:element name="{local-name()}">
        <xsl:if test="not(starts-with(local-name(), 'jforum_'))">
          <xsl:attribute name="id">
            <xsl:for-each select="$all[local-name() = local-name(current())]">
              <xsl:if test="generate-id() = generate-id($cur)">
                <xsl:value-of select="position()" />
              </xsl:if>
            </xsl:for-each>
          </xsl:attribute>
        </xsl:if>
        <xsl:copy-of select="@*|*" />
      </xsl:element>
    </xsl:for-each>
  </xsl:variable>

  <!-- Use EXSLT function to convert the RTF back to a node-set.  This gives a
       single-node set (a root node), so we need to extract the child elements
       -->
  <xsl:variable name="allElements" select="exslt:node-set($allElementsRTF)/*" />

  <!-- Utility variables with various subsets of the elements -->
  <xsl:variable name="roles" select="$allElements[local-name() = 'role']" />
  <xsl:variable name="asTypes"
          select="$allElements[local-name() = 'annotation_service_type']" />
  <xsl:variable name="srvs" select="$allElements[local-name() = 'service']" />


  <!-- Main entry point -->
  <xsl:template match="/">
    <dataset>
      <xsl:apply-templates select="$allElements" />
    </dataset>
  </xsl:template>


  <!-- Special handling for particular elements -->
  <xsl:template match="app_user">
    <app_user>
      <xsl:call-template name="copy-attrs"/>
    </app_user>
    <!-- Add role mappings -->
    <xsl:variable name="u" select="." />
    <xsl:for-each select="tw:role">
      <user_role user_id="{$u/@id}" role_id="{$roles[@name=current()/@name]/@id}" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="annotation_service">
    <!-- Determine the type ID and move parameters from element to attr -->
    <annotation_service
        annotation_service_type_id="{$asTypes[@name=current()/@tw:type]/@id}"
        parameters="{tw:parameters}">
      <xsl:call-template name="copy-attrs" />
    </annotation_service>
  </xsl:template>

  <xsl:template match="resource">
    <resource service_id="{$srvs[@name = current()/@tw:service]/@id}">
      <xsl:call-template name="copy-attrs" />
    </resource>
    <!-- Add role mappings -->
    <xsl:variable name="res" select="." />
    <xsl:for-each select="tw:role">
      <resource_role resource_id="{$res/@id}" role_id="{$roles[@name = current()/@name]/@id}" />
    </xsl:for-each>
  </xsl:template>

  <!-- Simple elements that just need to be copied -->
  <xsl:template match="*">
    <xsl:element name="{local-name()}">
      <xsl:copy-of select="@*" />
    </xsl:element>
  </xsl:template>

  <!-- Named utility templates -->
  <xsl:template name="copy-attrs">
    <xsl:copy-of select="@*[namespace-uri() = '']" />
  </xsl:template>

</xsl:stylesheet>
