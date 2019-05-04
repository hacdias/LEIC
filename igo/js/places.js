/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

const placesIcons = {
  restaurants: 'utensils',
  parks: 'tree',
  monuments: 'landmark',
  markets: 'store',
  diversions: 'chess',
  reservations: 'book'
}

function isFavourite (id) {
  for (const place of data.favourites) {
    if (place.id === id) {
      return true
    }
  }

  return false
}

function distanceSort (a, b) {
  return a.distance - b.distance
}

function canBeBooked (type) {
  return type === 'restaurants' ||
    type === 'diversions' ||
    type === 'monuments'
}

function getPlaces (kind) {
  if (kind === 'recommended') {
    return data.recommended
  }

  if (kind === 'favourites') {
    return data.favourites
  }

  if (kind === 'reservations') {
    return data.reservations
  }

  return data.places[kind].sort(distanceSort)
}

function getPlace (id) {
  for (const kind in data.places) {
    for (const place of data.places[kind]) {
      if (place.id === id) return place
    }
  }
}

function fillPlacesList (screen, kind, title) {
  const places = getPlaces(kind)
  const content = screen.querySelector('.content')
  content.innerHTML = ''
  updateScreenName(title)

  for (const place of places) {
    let el = getListTemplate(screen)
    if (place.isReserved) el.classList.add('current')
    el.dataset.args = place.id

    let kindIcon = el.querySelector('.kind-icon')
    kindIcon.classList.add(place.kind)
    kindIcon.classList.add(`fa-${placesIcons[place.kind]}`)

    el.querySelector('.name').innerHTML = place.name

    let infoIcon = el.querySelector('.info-icon')
    let info = el.querySelector('.info')

    if (kind === 'reservations') {
      infoIcon.classList.add('fa-book')
      info.innerHTML = place.reservationTime
    } else {
      infoIcon.classList.add('fa-ruler')
      info.innerHTML = `${place.distance}m`
    }

    content.appendChild(el)
  }

  if (places.length === 0) {
    screen.querySelector('.empty-content').classList.remove('dn')
  } else {
    screen.querySelector('.empty-content').classList.add('dn')
  }

  enableGoto(content)
}

function getScore (container, rating) {
  var score = 0

  container.innerHTML = ''

  while (score < rating - 0.5) {
    container.innerHTML += `<i class="fas fa-star"></i>`
    score++
  }
  if (rating - score === 0.5) {
    container.innerHTML += `<i class="fas fa-star-half-alt"></i>`
    score++
  }

  while (score < 5) {
    container.innerHTML += `<i class="far fa-star"></i>`
    score++
  }
}

function fillPlaceDesc (screen, id) {
  const place = getPlace(id)

  getScore(screen.querySelector('.rating'), place.rating)

  if (place.price && place.price > 0) {
    screen.querySelector('.price-para').classList.remove('dn')
    screen.querySelector('.price').innerHTML = place.price
  } else {
    screen.querySelector('.price-para').classList.add('dn')
  }

  const canBook = canBeBooked(place.kind)

  const bookBtn = screen.querySelector('.book-btn')
  const unbookBtn = screen.querySelector('.unbook-btn')

  bookBtn.dataset.args = id
  unbookBtn.dataset.args = id

  bookBtn.classList.add('dn')
  unbookBtn.classList.add('dn')

  if (canBook && place.isReserved) {
    unbookBtn.classList.remove('dn')
  } else if (canBook && !place.isReserved) {
    bookBtn.classList.remove('dn')
  }

  const favBtn = screen.querySelector('.fav-btn')
  const unfavBtn = screen.querySelector('.unfav-btn')

  favBtn.dataset.args = id
  unfavBtn.dataset.args = id

  if (isFavourite(id)) {
    favBtn.classList.add('dn')
    unfavBtn.classList.remove('dn')
  } else {
    favBtn.classList.remove('dn')
    unfavBtn.classList.add('dn')
  }

  screen.querySelector('.goto-gps').dataset.args = id
}

function onGotoGpsClick (el) {
  const place = getPlace(el.dataset.args)

  confirmationBox({
    question: `Deseja ir para: ${place.name}?`,
    rightHandler: () => {
      showScreen('gps-path', el)
    }
  })
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

function clearBooking () {
  document.getElementById('new-booking-people').value = ''
  document.getElementById('new-booking-time').value = ''
  document.getElementById('new-booking-date').value = ''
  document.querySelector('#place-booking .book-btn').classList.add('disabled')
}

function fillBookingDetails (screen, id) {
  window.alert('TODO: ADD NEW PICKER')

  newPicker(screen, '#new-booking-date', 'Data', 'YYYY-MM-DD')
  newPicker(screen, '#new-booking-time', 'Hora', 'HH:mm')

  screen.querySelector('.book-btn').dataset.args = id
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
    document.querySelector('#place-booking .book-btn').classList.remove('disabled')
  } else {
    document.querySelector('#place-booking .book-btn').classList.add('disabled')
  }
}

function removeReservation (el) {
  let place = getPlace(el.dataset.args)

  confirmationBox({
    question: 'Deseja cancelar a sua reserva?',
    rightClass: 'cancel',
    rightHandler: () => {
      place.isReserved = false
      const index = data.reservations.findIndex(el => el.id === place.id)
      data.reservations.splice(index, 1)
      runAndBack()
    }
  })
}

function createBooking (el) {
  let place = getPlace(el.dataset.args)
  let data = validateNewBooking()
  if (!data) {
    // this shouldn't happen, but we never know!
    return
  }

  confirmationBox({
    question: `Confirma a reserva para ${data.people} pessoas, na data:\n ${data.time}? Terá o custo total de ${numberWithSpaces(data.people * place.price)}€.`,
    rightHandler: () => {
      place.isReserved = true
      place.reservationPeople = data.people
      place.reservationTime = data.time
      window.data.reservations.push(place)

      if (window.data.currentBudget) {
        window.data.currentBudget.expenses.push({
          name: place.name,
          value: data.people * el.price
        })
      }

      runAndBack(clearBooking)
    }
  })
}

function addFavourite (el) {
  const place = getPlace(el.dataset.args)

  confirmationBox({
    question: 'Deseja adicionar este local à sua lista de favoritos?',
    rightHandler: () => {
      data.favourites.push(place)
      runAndBack()
    }
  })
}

function removeFavourite (el) {
  const place = getPlace(el.dataset.args)

  confirmationBox({
    question: 'Deseja remover este local da sua lista de favoritos?',
    rightClass: 'cancel',
    rightHandler: () => {
      const index = data.favourites.findIndex(el => el.id === place.id)
      data.favourites.splice(index, 1)
      runAndBack()
    }
  })
}

function placesBootstrap () {
  enableKeybaordFor(document.getElementById('new-booking-people'), false)
}
