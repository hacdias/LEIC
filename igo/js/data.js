window.data = {
  people: [
    {
      name: 'Maria João',
      distance: 100,
      picture: 'maria_joao.png'
    },
    {
      name: 'Manuel Jesus',
      distance: 135,
      picture: 'manuel_jesus.png'
    },
    {
      name: 'Tiago Barroso',
      distance: 15,
      picture: 'tiago_barroso.jpg'
    },
    {
      name: 'Isabel Soares',
      distance: 25,
      picture: 'isabel_soares.jpg'
    },
    {
      name: 'Ana Silva',
      distance: 4,
      picture: 'ana_silva.jpg'
    },
    {
      name: 'Avó Celeste',
      distance: 32,
      picture: 'avo_celeste.jpg'
    },
    {
      name: 'Primo Joaquim',
      distance: 45,
      picture: 'joaquim_fonseca.jpg'
    }
  ],
  places: {
    restaurants: [
      {
        name: "Tubarão O'Mar",
        distance: 23,
        rating: 4.5,
        price: 25,
        kind: "restaurants"
      },
      {
        name: 'Monte do Zé',
        distance: 89,
        rating: 2.5,
        price: 7.5,
        kind: "restaurants"
      },
      {
        name: 'Sabores do Mar',
        distance: 55,
        rating: 5,
        price: 13,
        kind: "restaurants"
      },
      {
        name: 'Prego no Espeto',
        distance: 28,
        rating: 4,
        price: 6,
        kind: "restaurants"
      }
    ],
    parks: [
      {
        name: 'Quinta das Conchas',
        distance: 150,
        rating: 4,
        kind: "parks"
      },
      {
        name: 'Bela Vista',
        distance: 980,
        rating: 5,
        kind: "parks"
      },
      {
        name: 'Jardim do Torel',
        distance: 550,
        rating: 2.5,
        kind: "parks"
      },
      {
        name: 'Eduardo VII',
        distance: 200,
        rating: 5,
        kind: "parks"
      },
      {
        name: 'Arco do Cego',
        distance: 5,
        rating: 4,
        kind: "parks"
      },
      {
        name: 'Gulbenkian',
        distance: 150,
        rating: 5,
        kind: "parks"
      }
    ],
    monuments: [
      {
        name: 'Templo de Diana',
        distance: 1350,
        rating: 5,
        kind: "monuments"
      },
      {
        name: 'Torre de Belém',
        distance: 135,
        rating: 3.5,
        kind: "monuments"
      },
      {
        name: 'Padrão dos Descobrimentos',
        distance: 350,
        rating: 4,
        kind: "monuments"
      }
    ],
    markets: [
      {
        name: 'Mercado da Ribeira',
        distance: 345,
        rating: 4,
        kind: "markets"
      }
    ],
    diversions: [
      {
        name: 'Euro Fun',
        distance: 450,
        rating: 3.5,
        kind: "diversions"
      }
    ]
  },
  favourites: [],
  recommended: [],
  currentBudget: null,
  budgets: [
    {
      id: uuidv4(),
      name: 'Glasgow Trip',
      date: new Date(2019, 2),
      budget: 2300,
      expenses: [
        {
          name: 'Restaurante A Ribeira',
          value: 50
        },
        {
          name: 'Templo de Diana',
          value: 30
        },
        {
          name: 'Bela Vista',
          value: 22200
        }
      ]
    },
    {
      id: uuidv4(),
      name: 'Maldivas',
      date: new Date(2015, 3),
      budget: 3500,
      expenses: [
        {
          name: 'Euro Fun',
          value: 40
        },
        {
          name: 'Jardim do Torel',
          value: 5
        },
        {
          name: 'Restaurante Tubarão O\'Mar',
          value: 80
        }
      ]
    },
    {
      id: uuidv4(),
      name: 'Viagem ao Brasil',
      date: new Date(2012, 11),
      budget: 1200,
      expenses: [
        {
          name: 'Restaurante A Ribeira',
          value: 50
        },
        {
          name: 'Templo de Diana',
          value: 30
        },
        {
          name: 'Bela Vista',
          value: 720
        }
      ]
    },
    {
      id: uuidv4(),
      name: 'Japón',
      date: new Date(2016, 6),
      budget: 3500,
      expenses: [
        {
          name: 'Euro Fun',
          value: 40
        },
        {
          name: 'Jardim do Torel',
          value: 5
        },
        {
          name: 'Restaurante Tubarão O\'Mar',
          value: 80
        }
      ]
    }
  ]
}
