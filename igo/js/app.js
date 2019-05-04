function setClocks () {
  const norm = i => i < 10 ? `0${i}` : i
  const today = new Date()
  const hour = norm(today.getHours())
  const mins = norm(today.getMinutes())
  const clocks = document.querySelectorAll('.clock')

  for (const clock of clocks) {
    clock.innerHTML = `${hour}:${mins}`
  }

  setTimeout(setClocks, (60 - today.getSeconds()) * 1000)
}

let history = []

function showScreen (name, el) {
  let args = (el ? (el.dataset.args || '') : '').trim().split('#')

  if (name === '--back') {
    history.pop()
    name = history[history.length - 1].name
    args = history[history.length - 1].args
  } else {
    history.push({ name, args })
  }

  const screens = document.querySelectorAll('.screen')
  let currentScreen = null

  for (const screen of screens) {
    if (screen.id === name) {
      currentScreen = screen
      screen.classList.add('active')
    } else {
      screen.classList.remove('active')
    }
  }

  turnOffFlashLight()
  if (currentScreen.dataset.call) {
    window[currentScreen.dataset.call](currentScreen, ...args)
  }

  if (name === 'lockscreen') {
    document.querySelector('#status .clock').style.opacity = 0
    document.querySelector('#backbar').style.display = 'none'
  } else {
    document.querySelector('#status .clock').style.opacity = 1
    document.querySelector('#backbar').style.display = ''
    if (currentScreen.dataset.name) updateScreenName(currentScreen.dataset.name)
  }

  if (name === 'mainmenu') {
    document.querySelector('#only-mainmenu').style.display = ''
    document.querySelector('#not-mainmenu').style.display = 'none'
  } else {
    document.querySelector('#not-mainmenu').style.display = ''
    document.querySelector('#only-mainmenu').style.display = 'none'
  }

  document.querySelector('#screen-space').scrollTop = 0
}

function turnOffFlashLight () {
  document.body.classList.remove('flashlight')
}

function turnOnFlashLight (screen) {
  document.body.classList.add('flashlight')
}

function distanceSort (a, b) {
  return a.distance - b.distance
}

const placesIcons = {
  restaurants: 'utensils',
  parks: 'tree',
  monuments: 'landmark',
  markets: 'store',
  diversions: 'chess',
  reservations: 'book'
}

function newPicker (screen, selector, title, format) {
  return new Picker(screen.querySelector(selector), {
    container: document.querySelector('#watch'),
    text: {
      title: title,
      cancel: 'Cancelar',
      confirm: 'OK'
    },
    format: format,
    rows: 3
  })
}

function bookRestaurant (screen, name, price) {
  screen.innerHTML = `
  <input placeholder="Pessoas (12 máx)" type="text" id="new-booking-people" onchange="onNewBookingChange()"></input>
  <div>
    <input id="new-booking-date" placeholder="Data" type="text" onchange="onNewBookingChange()"></input>
    <input id="new-booking-time" placeholder="Horas" type="text" onchange="onNewBookingChange()"></input>
  </div>`

  console.log(name)

  newPicker(screen, '#new-booking-people', 'Pessoas', 'MM')
  newPicker(screen, '#new-booking-date', 'Data', 'YYYY-MM-DD')
  newPicker(screen, '#new-booking-time', 'Hora', 'HH:mm')

  let el = document.createElement('div')
  el.classList.add('flex')
  el.classList.add('f-50')

  let el1 = document.createElement('button')
  el1.classList.add('cancel')
  el1.addEventListener('click', () => {
    runAndBack(clearBooking)
  })
  el1.innerHTML = `Cancelar`

  el.appendChild(el1)

  let el2 = document.createElement('button')
  el2.setAttribute('id', 'new-booking-submit')
  el2.classList.add('ok')
  el2.classList.add('disabled')
  el2.addEventListener('click', () => {
    createBooking(name, price)
  })
  el2.innerHTML = `Reservar`

  el.appendChild(el2)
  screen.appendChild(el)
}

function getPlaces (kind) {
  if (kind === 'recommended') {
    return window.data.recommended
  }

  if (kind === 'favourites') {
    return window.data.favourites
  }

  if (kind === 'reservations') {
    return window.data.reservations
  }

  return window.data.places[kind].sort(distanceSort).map(place => {
    return place
  })
}

function updatePlaces (screen, skind, title) {
  const places = getPlaces(skind)
  screen.innerHTML = ''

  updateScreenName(title)

  for (const { name, distance, rating, kind, price = 0, isReserved, map, reservationTime } of places) {
    let el = document.createElement('div')
    el.classList.add('item')
    if (isReserved) {
      el.classList.add('current')
    }
    el.dataset.args = `${name}#${distance}#${rating}#${map}#${price}`

    el.innerHTML = `<div>
        <i class="${kind} fas fa-${placesIcons[kind]}"></i>
      </div>`

    if (skind === 'reservations') {
      el.innerHTML += `
      <div>
        <p>${name}</p>
        <p><i class="fas fa-book"></i> ${reservationTime} </p>
      </div>`
    } else {
      el.innerHTML += `
      <div>
        <p>${name}</p>
        <p><i class="fas fa-ruler"></i> ${distance}m</p>
      </div>`
    }

    el.addEventListener('click', () => {
      showScreen('place-desc', el)
    })

    screen.appendChild(el)
  }

  if (places.length === 0) {
    screen.innerHTML = `<p>Não há locais nesta categoria.</p>`
  }
}

function getScore (screen, rating) {
  var score = 0

  let container = document.createElement('div')
  container.innerHTML = `<p>Classificação</p>`

  let el = document.createElement('div')
  el.classList.add('rating')

  while (score < rating - 0.5) {
    el.innerHTML += `<i class="fas fa-star"></i>`
    score++
  }
  if (rating - score == 0.5) {
    el.innerHTML += `<i class="fas fa-star-half-alt"></i>`
    score++
  }
  while (score < 5) {
    el.innerHTML += `<i class="far fa-star"></i>`
    score++
  }

  container.appendChild(el)
  screen.appendChild(container)
}

function removeReservation (place) {
  let name = place.name
  console.log(name)

  confirmationBox({
    question: `Deseja cancelar a sua reserva?`,
    rightClass: 'cancel',
    rightHandler: () => {
      place.isReserved = false
      var i = 0
      for (const place of window.data.reservations) {
        if (place.name == name) {
          window.data.reservations.splice(i, 1)
        }
        i++
      }
      runAndBack()
    }
  })
}

function canBeBooked (type) {
  return type === 'restaurants' ||
    type === 'diversions' ||
    type === 'monuments'
}

function updatePlaceInfo (screen, name, distance, rating, map, price) {
  updateScreenName(name)
  screen.innerHTML = ''

  getScore(screen, rating)

  if (price != 0) {
    screen.innerHTML += `<p style="margin: 0.5em 0;font-size: 0.8em;">${numberWithSpaces(price)}€ por pessoa.</p>`
  }

  let el1 = document.createElement('button')
  el1.classList.add('blue')
  el1.dataset.args = `${name}#${distance}#${map}`
  el1.innerHTML = `<i class="fas fa-directions"></i> Ir`
  el1.addEventListener('click', () => {
    gotoGPS(el1)
  })
  screen.appendChild(el1)

  var place = getPlace(name)

  if (canBeBooked(place.kind) && !place.isReserved) {
    let el = document.createElement('button')
    el.classList.add('blue')
    el.innerHTML = `<i class="fas fa-book-open"></i> Reservar`
    el.dataset.args = `${name}#${price}`
    el.addEventListener('click', () => {
      showScreen('restaurant-booking', el)
    })
    screen.appendChild(el)
  } else if (canBeBooked(place.kind) && place.isReserved) {
    let el = document.createElement('button')
    el.classList.add('cancel')
    el.innerHTML = `<i class="fas fa-book-open"></i> Cancelar Reserva`
    el.dataset.args = `${name}#${price}`
    el.addEventListener('click', () => {
      removeReservation(place)
    })
    screen.appendChild(el)
  }

  let el = document.createElement('button')
  if (isFavourite(name)) {
    el.classList.add('cancel')
    el.innerHTML = `<i class="fas fa-star"></i> Remover dos Favoritos`
    el.addEventListener('click', () => {
      removeFavourite()
    })
  } else {
    el.classList.add('blue')
    el.innerHTML = `<i class="fas fa-star"></i> Adicionar aos Favoritos`
    el.addEventListener('click', () => {
      addFavourite()
    })
  }
  screen.appendChild(el)

  // TODO fazer o menu c/ classificaç\ao, distancia, numero de pessoas e possibilidade de encaminhar pro GPS
  // se for restaurante meter botao reservar.

  console.log(name, distance, rating)
}

function gotoGPS (el) {
  let name = document.querySelector('#current-screen-name').innerHTML
  console.log(name)

  confirmationBox({
    question: `Deseja ir para: ` + name + `?`,
    rightHandler: () => {
      showScreen('gps-path', el)
    }
  })
}

function getPlace (name) {
  for (const place of window.data.places.restaurants) {
    if (place.name == name) {
      return place
    }
  }
  for (const place of window.data.places.parks) {
    if (place.name == name) {
      return place
    }
  }
  for (const place of window.data.places.monuments) {
    if (place.name == name) {
      return place
    }
  }
  for (const place of window.data.places.markets) {
    if (place.name == name) {
      return place
    }
  }
  for (const place of window.data.places.diversions) {
    if (place.name == name) {
      return place
    }
  }
}

function isFavourite (name) {
  for (const place of window.data.favourites) {
    if (place.name == name) {
      return place
    }
  }
}

function addFavourite () {
  let name = document.querySelector('#current-screen-name').innerHTML
  console.log(name)

  confirmationBox({
    question: `Deseja adicionar este local à sua lista de favoritos?`,
    rightHandler: () => {
      window.data.favourites.push(getPlace(name))
      console.log(getPlace(name))
      console.log(window.data.favourites)
      runAndBack()
    }
  })
}

function removeFavourite () {
  let name = document.querySelector('#current-screen-name').innerHTML
  console.log(name)

  confirmationBox({
    question: `Deseja remover este local da sua lista de favoritos?`,
    rightClass: 'cancel',
    rightHandler: () => {
      var i = 0
      for (const place of window.data.favourites) {
        if (place.name == name) {
          window.data.favourites.splice(i, 1)
        }
        i++
      }
      console.log(getPlace(name))
      console.log(window.data.favourites)
      runAndBack()
    }
  })
}

function runAndBack (...fns) {
  for (const fn of fns) {
    fn()
  }
  showScreen('--back')
}

function clearBooking () {
  document.getElementById('new-booking-people').value = ''
  document.getElementById('new-booking-time').value = ''
  document.getElementById('new-booking-date').value = ''
  document.getElementById('new-booking-submit').classList.add('disabled')
}

function validateNewBooking () {
  let people = document.getElementById('new-booking-people').value
  let hours = document.getElementById('new-booking-time').value
  let date = document.getElementById('new-booking-date').value
  let time = `${date} ${hours}`

  if (people <= 0 || people > 16 || !hours || !date) {
    return
  }

  let bookingTime = new Date(time)

  if (bookingTime < new Date(new Date().getTime() + 30 * 60000)) {
    return
  }

  return {
    people,
    time
  }
}

function onNewBookingChange () {
  if (validateNewBooking()) {
    document.getElementById('new-booking-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-booking-submit').classList.add('disabled')
  }
}

function createBooking (name, price) {
  let data = validateNewBooking()
  if (!data) {
    // this shouldn't happen, but we never know!
    return
  }

  confirmationBox({
    question: `Confirma a reserva para ${data.people} pessoas, na data:\n ${data.time}? Terá o custo total de ${numberWithSpaces(data.people * price)}€.`,
    rightHandler: () => {
      var place = getPlace(name)
      place.isReserved = true
      place.reservationPeople = data.people
      place.reservationTime = data.time
      window.data.reservations.push(place)

      if (window.data.currentBudget) {
        window.data.currentBudget.expenses.push({
          name,
          value: data.people * price
        })
      }

      runAndBack(clearBooking)
    }
  })
}

function getTextDate (date) {
  const year = date.getFullYear()
  const month = date.getMonth() < 10 ? `0${date.getMonth()}` : date.getMonth()
  return `${year} - ${month}`
}

function createRecommended () {
  var restaurants = window.data.places.restaurants
  var monuments = window.data.places.monuments
  var markets = window.data.places.markets
  var diversions = window.data.places.diversions
  var parks = window.data.places.parks
  var all = restaurants.concat(monuments, markets, diversions, parks)

  var recommended = []
  var random = Math.floor(Math.random() * (all.length - 9)) + 3

  i = 0
  while (i < random) {
    var n = Math.floor(Math.random() * all.length)
    recommended.push(all[n])
    all.splice(n, 1)
    i++
  }

  window.data.recommended = recommended
  console.log(recommended)
}

// TODO: change
function showGpsPath (screen, name, distance, map) {
  screen.innerHTML = ''

  screen.style.backgroundImage = `url(./assets/maps/${map}.png)`
  console.log(screen, name, distance, map)

  let el = document.createElement('button')
  el.classList.add('goto')
  el.setAttribute('id', 'start-route-button')
  el.dataset.args = `${name}#${distance}#${map}`
  el.innerHTML = `Começar`

  el.addEventListener('click', () => {
    startGpsAnimation(screen, map)
  })
  screen.appendChild(el)
}

function startGpsAnimation (screen, map) {
  screen.innerHTML = ''
  let i = 0

  const update = () => {
    if (i < 10) {
      screen.style.backgroundImage = `url(./assets/maps/${map}-${++i}.png)`
    } else {
      let el = document.createElement('button')
      el.classList.add('goto')
      el.setAttribute('id', 'end-route-button')
      el.innerHTML = `Concluir`

      el.addEventListener('click', () => {
        runAndBack()
      })
      screen.appendChild(el)
    }
  }

  const interval = setInterval(update, 1000)

  const stop = () => {
    clearInterval(interval)
    document.querySelector('[data-to=--back]').removeEventListener('click', stop)
  }

  document.querySelector('[data-to=--back]').addEventListener('click', stop)
}

let images = []
function preload () {
  for (let i = 1; i < 16; i++) {
    let m = new Image()
    m.src = `./assets/maps/${i}.png`
    images.push(m)

    for (let j = 0; j <= 10; j++) {
      let p = new Image()
      p.src = `./assets/maps/${i}-${j}.png`
      images.push(p)
    }
  }
}

function startup () {
  preload()
  setClocks()
  createRecommended()

  enableGoto()

  document.getElementById('gps-input').addEventListener('keyup', event => {
    const value = event.currentTarget.value.toLowerCase()
    let res = document.querySelector('.gps-results')
    res.innerHTML = ''

    for (const key in window.data.places) {
      for (const { name, distance, map } of window.data.places[key]) {
        if (name.toLowerCase().includes(value)) {
          let li = document.createElement('li')
          li.dataset.args = `${name}#${distance}#${map}`
          li.innerHTML = name
          li.addEventListener('click', () => {
            showScreen('gps-path', li)
          })
          res.appendChild(li)
        }
      }
    }
  })

  showScreen('lockscreen')
}

startup()
