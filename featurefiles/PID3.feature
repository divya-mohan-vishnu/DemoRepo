@browser=CHROME
Feature:ETAFDemo
BDD Suite Description

@ETAF
Scenario:ETAFDEMO_GetNextInvoiceValue

Given I trigger the Service Call using Endpoint URL
| ReferenceId | CCServiceCall_GetNext |

And I validate the Service Call Response with the TestData Inputs
| ReferenceId | ResponseValidation_ValidRequest_GetNext |

#@ETAF
Scenario:ETAFDEMO_GetReissInvoiceValue

Given I trigger the Service Call using Endpoint URL
| ReferenceId | CCServiceCall_GetReiss |

And I validate the Service Call Response with the TestData Inputs
| ReferenceId | ResponseValidation_ValidRequest_GetReiss |



@ETAF
Scenario:ETAFDEMO_GetNextInvoiceValueS

Given I trigger the Service Call using Endpoint URL
| ReferenceId | CCServiceCall_GetNext |

And I validate the Service Call Response with the TestData Inputs
| ReferenceId | ResponseValidation_ValidRequest_GetNext |

And I validate the Database values with the TestData Inputs
| ReferenceId | DBTableValidation_tc14 |


#@ETAF
Scenario:ETAFDEMO_PostScenario

Given I trigger the Service Call using Endpoint URL with post content
| ReferenceId | CCServiceCall_GetBookByVolumeIdPOST_2 |

And I validate the Service Call Response with the TestData Inputs
| ReferenceId | ResponseValidation_ValidatePOSTRequest |

And I validate the Database values with the TestData Inputs
| ReferenceId | DBTableValidation_tc14 |
