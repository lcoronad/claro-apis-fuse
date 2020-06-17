[
	{
		"href": "https://host:port/tmf-api/customerManagement/v3/customer/1140",
		"id": "1140",
		"name": "Moon Football Club",
		"status": "Approved",
		"statusReason": "Account details checked",
		"validFor": {
			"startDateTime": "2018-06-12T00:00",
			"endDateTime": "2019-01-11T00:00"
		},
		"engagedParty": [
			{
				"href": "https://host:port/tmf-api/partyManagement/v2/organization/500",
				"id": "500",
				"name": "Happy Travellers"
			}
		],
		"account": [
			{
				"description": "This account ...",
				"href": "https://host:port/tmf-api/accountManagement/v2/partyAccount/8251",
				"id": "8251",
				"name": "Travel Account"
			}
		],
		"paymentMethod": [
			{
				"href": "https://host:port/tmf-api/paymentMethods/v2/paymentMethod/9562",
				"id": "9562",
				"name": "professional payment"
			}
		],
		"contactMedium": [
			{
				"preferred": false,
				"type": "TelephoneNumber",
				"validFor": {
					"startDateTime": "2018-06-13T00:00",
					"endDateTime": "2019-01-11T00:00"
				},
				"characteristic": {
					"city": "Paris",
					"country": "France",
					"emailAddress": "alain.delon@best-actor.fr",
					"postCode": "75014",
					"street1": "15 Rue des Canards",
					"type": "home"
				}
			}
		],
		"characteristic": [
			{
				"name": "fidelityProgram",
				"value": "premium"
			}
		],
		"creditProfile": [
			{
				"creditProfileDate": "2018-06-15T00:00",
				"creditRiskRating": 4,
				"creditScore": 5,
				"validFor": {
					"startDateTime": "2018-06-13T00:00",
					"endDateTime": "2019-01-11T00:00"
				}
			}
		],
		"agreement": [
			{
				"href": "https://host:port/tmf-api/agreementManagement/v2/agreement/4721",
				"id": "4721",
				"name": "Summer Contract Agreement"
			}
		],
		"relatedParty": [
			{
				"href": "https://host:port/tmf-api/partyManagement/v2/organization/2777",
				"id": "2777",
				"name": "John Doe",
				"role": "bill receiver"
			}
		]
	}
]