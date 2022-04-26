## Legends Utils

A collection of useful utilities for the SWG space

### TOCBuilder

This utility takes input of existing TOC and TRE (v5 and below) files to output a singular new TOC.
An example of usage is `java -jar TOCBuilder.jar output_toc_name.toc sku0_client.toc sku1_client.toc patch_60_client_00.tre`
The input files should be listed as you would in `live.cfg`, from bottom to top. Later files take precedence with duplicates.