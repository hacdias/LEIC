window.data = {
  people: [
    {
      name: 'Maria João',
      distance: 100,
      picture: 'maria_joao.png',
      phone: '+351 946 765 435',
      lastSeen: 14, // minutos
      messages: []
    },
    {
      name: 'Manuel Jesus',
      distance: 135,
      picture: 'manuel_jesus.png',
      phone: '+351 945 345 234',
      lastSeen: 13, // minutos,
      messages: [],
      calls: [{
        time: new Date(),
        incoming: false,
        missed: false,
        duration: 23 // minutes
      }]
    },
    {
      name: 'Tiago Barroso',
      distance: 15,
      picture: 'tiago_barroso.jpg',
      phone: '+351 946 234 346',
      lastSeen: 12, // minutos,
      messages: [{
        from: true,
        message: 'Alô. Já chegaste?'
      },
      {
        message: 'Estou quase'
      }],
      calls: []
    },
    {
      name: 'Isabel Soares',
      distance: 25,
      picture: 'isabel_soares.jpg',
      phone: '+351 956 765 338',
      lastSeen: 74, // minutos,
      messages: [],
      calls: []
    },
    {
      name: 'Ana Silva',
      distance: 4,
      picture: 'ana_silva.jpg',
      phone: '+351 276 345 897',
      lastSeen: 589, // minutos,
      messages: [],
      calls: [{
        time: new Date(),
        incoming: true,
        missed: true
      }]
    },
    {
      name: 'Avó Celeste',
      distance: 32,
      picture: 'avo_celeste.jpg',
      phone: '+351 933 453 876',
      lastSeen: 239, // minutos,
      messages: [],
      calls: []
    },
    {
      name: 'Primo Joaquim',
      distance: 45,
      picture: 'joaquim_fonseca.jpg',
      phone: '+351 946 765 435',
      lastSeen: 234,
      messages: [],
      calls: []
    }
  ],
  places: {
    restaurants: [
      {
        name: "Tubarão O'Mar",
        distance: 23,
        rating: 4.5,
        price: 25,
        kind: 'restaurants',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      },
      {
        name: 'Monte do Zé',
        distance: 89,
        rating: 2.5,
        price: 7.5,
        kind: 'restaurants',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      },
      {
        name: 'Sabores do Mar',
        distance: 55,
        rating: 5,
        price: 13,
        kind: 'restaurants',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      },
      {
        name: 'Prego no Espeto',
        distance: 28,
        rating: 4,
        price: 6,
        kind: 'restaurants',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      }
    ],
    parks: [
      {
        name: 'Quinta das Conchas',
        distance: 150,
        rating: 4,
        kind: 'parks'
      },
      {
        name: 'Bela Vista',
        distance: 980,
        rating: 5,
        kind: 'parks'
      },
      {
        name: 'Jardim do Torel',
        distance: 550,
        rating: 2.5,
        kind: 'parks'
      },
      {
        name: 'Eduardo VII',
        distance: 200,
        rating: 5,
        kind: 'parks'
      },
      {
        name: 'Arco do Cego',
        distance: 5,
        rating: 4,
        kind: 'parks'
      },
      {
        name: 'Gulbenkian',
        distance: 150,
        rating: 5,
        kind: 'parks'
      }
    ],
    monuments: [
      {
        name: 'Templo de Diana',
        distance: 1350,
        rating: 5,
        price: 3,
        kind: 'monuments',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      },
      {
        name: 'Torre de Belém',
        distance: 135,
        rating: 3.5,
        price: 1.5,
        kind: 'monuments',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      },
      {
        name: 'Notre Dame',
        distance: 350,
        rating: 4,
        price: 2,
        kind: 'monuments',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      }
    ],
    markets: [
      {
        name: 'Mercado da Ribeira',
        distance: 345,
        rating: 4,
        kind: 'markets'
      }
    ],
    diversions: [
      {
        name: 'Euro Fun',
        distance: 450,
        rating: 3.5,
        price: 7,
        kind: 'diversions',
        isReserved: false,
        reservationTime: '00:00',
        reservationPeople: 0
      }
    ]
  },
  favourites: [],
  recommended: [],
  reservations: [],
  currentBudget: {
    id: uuidv4(),
    name: 'Viagem ao Técnico',
    date: new Date(),
    budget: 5000,
    expenses: []
  },
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

for (const key in window.data.places) {
  for (let i = 0; i < window.data.places[key].length; i++) {
    window.data.places[key][i].map = Math.floor(Math.random() * 16) + 1
  }
}
