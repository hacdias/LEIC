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

function updateScreenName (name) {
  document.querySelector('#current-screen-name').innerHTML = name
}

function numberWithSpaces (x) {
  var parts = x.toString().split('.')
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ' ')
  return parts.join('.')
}

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

function updatePeople (screen) {
  const people = window.data.people.sort(distanceSort)
  screen.innerHTML = ''

  for (const { name, distance, picture } of people) {
    let el = document.createElement('div')
    el.classList.add('item')
    el.innerHTML = `<div>
        <img src="assets/people/${picture}">
      </div>
      <div>
        <p>${name}</p>
        <p><i class="fas fa-ruler"></i> ${distance}m</p>
      </div>`

    el.addEventListener('click', () => {
      window.alert('Não implementado: visitar ' + name)
    })

    screen.appendChild(el)
  }
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

function bookRestaurant (screen, name) {
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
    createBooking(name)
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

  for (const { name, distance, rating, kind, isReserved, map, reservationPeople, reservationTime } of places) {
    let el = document.createElement('div')
    el.classList.add('item')
    if (isReserved) {
      el.classList.add('current')
    }
    el.dataset.args = `${name}#${distance}#${rating}#${map}`

    el.innerHTML = `<div>
        <i class="${kind} fas fa-${placesIcons[kind]}"></i>
      </div>`

    if (skind === 'reservations') {
      el.innerHTML += `
      <div>
        <p>${name}</p>
        <p><i class="fas fa-book"></i> ${reservationTime} </p>
      </div>`
    }
    else {
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

function updatePlaceInfo (screen, name, distance, rating, map) {
  updateScreenName(name)
  screen.innerHTML = ''

  getScore(screen, rating)

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
    el.dataset.args = `${name}`
    el.addEventListener('click', () => {
      showScreen('restaurant-booking', el)
    })
    screen.appendChild(el)
  } else if (canBeBooked(place.kind) && place.isReserved) {
    let el = document.createElement('button')
    el.classList.add('cancel')
    el.innerHTML = `<i class="fas fa-book-open"></i> Cancelar Reserva`
    el.dataset.args = `${name}`
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

function getBudgets () {
  return window.data.budgets.sort((a, b) => {
    if (a.active && !b.active) return 1
    if (b.active && !a.active) return 1
    if (a.date > b.date) return -1
    if (b.date > a.date) return 1
    return 0
  })
}

function runAndBack (...fns) {
  for (const fn of fns) {
    fn()
  }
  showScreen('--back')
}

function clearBudget () {
  document.getElementById('new-budget-name').value = ''
  document.getElementById('new-budget-value').value = ''
  document.getElementById('new-budget-submit').classList.add('disabled')
}
function clearBooking () {
  document.getElementById('new-booking-people').value = ''
  document.getElementById('new-booking-time').value = ''
  document.getElementById('new-booking-date').value = ''
  document.getElementById('new-booking-submit').classList.add('disabled')
}

function validateNewBudget () {
  let name = document.getElementById('new-budget-name').value
  let value = parseFloat(document.getElementById('new-budget-value').value)

  if (name === '' || isNaN(value)) {
    // this shouldn't happen, but we never know!
    return
  }

  if (value <= 0) {
    return
  }

  return { name, value }
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

function onNewBudgetChange () {
  if (validateNewBudget()) {
    document.getElementById('new-budget-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-budget-submit').classList.add('disabled')
  }
}

function confirmationBox ({
  question,
  leftMsg = 'Não',
  rightMsg = 'Sim',
  leftClass = '',
  rightClass = 'ok',
  leftHandler,
  rightHandler,
  anywaysHandler
}) {
  const overlay = document.getElementById('overlay')
  const box = document.getElementById('confirmation-box')
  const leftButton = box.querySelector('#left-confirm')
  const rightButton = box.querySelector('#right-confirm')

  overlay.classList.add('active')
  box.classList.add('active')

  box.querySelector('p').innerHTML = question
  leftButton.innerHTML = leftMsg
  rightButton.innerHTML = rightMsg
  leftButton.className = leftClass
  rightButton.className = rightClass

  const reset = () => {
    leftButton.removeEventListener('click', leftAction)
    rightButton.removeEventListener('click', rightAction)
    overlay.classList.remove('active')
    box.classList.remove('active')
  }

  const leftAction = () => {
    if (leftHandler) leftHandler()
    if (anywaysHandler) anywaysHandler()
    reset()
  }

  const rightAction = () => {
    if (rightHandler) rightHandler()
    if (anywaysHandler) anywaysHandler()
    reset()
  }

  leftButton.addEventListener('click', leftAction)
  rightButton.addEventListener('click', rightAction)
}

function createBooking (name) {
  let data = validateNewBooking()
  if (!data) {
    // this shouldn't happen, but we never know!
    return
  }
  console.log(name)
  confirmationBox({
    question: `Confirma a reserva para ${data.people} pessoas, na data:\n ${data.time} ?`,
    rightHandler: () => {
      var place = getPlace(name)
      place.isReserved = true
      place.reservationPeople = data.people
      place.reservationTime = data.time
      window.data.reservations.push(place)

      console.log(getPlace(name))
      console.log(window.data.reservations)

      runAndBack(clearBooking)
    }
  })
}

function createBudget () {
  if (window.data.currentBudget) {
    // this shouldn't happen, but we never know!
    return
  }

  let data = validateNewBudget()
  if (!data) {
    // this shouldn't happen, but we never know!
    return
  }

  confirmationBox({
    question: `Confirma a criação de um orçamento de valor ${numberWithSpaces(data.value)} €?`,
    rightHandler: () => {
      window.data.currentBudget = {
        id: uuidv4(),
        name: data.name,
        budget: data.value,
        date: new Date(),
        expenses: []
      }

      runAndBack(clearBudget)
    }
  })
}

function deleteBudget () {
  confirmationBox({
    question: `Deseja remover o orçamento atual?`,
    rightClass: 'cancel',
    rightHandler: () => {
      window.data.currentBudget = null
      runAndBack()
    }
  })
}

function endBudget () {
  confirmationBox({
    question: `Deseja terminar a viagem atual?`,
    rightHandler: () => {
      window.data.budgets.push(window.data.currentBudget)
      window.data.currentBudget = null
      runAndBack()
    }
  })
}

function getTextDate (date) {
  const year = date.getFullYear()
  const month = date.getMonth() < 10 ? `0${date.getMonth()}` : date.getMonth()
  return `${year} - ${month}`
}

function getBudgetElement ({ id, name, date }, current = false) {
  let el = document.createElement('div')
  el.classList.add('item')
  if (current) el.classList.add('current')
  el.dataset.args = `${id}#${current}`
  el.innerHTML = `<div>
      <i class="fas fa-plane"></i>
    </div>
    <div>
      <p>${name}</p>
      <p><i class="far fa-calendar-alt"></i> ${getTextDate(date)}</p>
    </div>`

  el.addEventListener('click', () => {
    showScreen('budget-desc', el)
  })

  return el
}

function listBudgets (screen) {
  let budgets = getBudgets()
  console.log(window.data.currentBudget)

  let budgetsDiv = screen.querySelector('#budget-list')

  budgetsDiv.innerHTML = ''

  if (window.data.currentBudget === null) {
    document.getElementById('new-budget-button').classList.remove('dn')
  } else {
    document.getElementById('new-budget-button').classList.add('dn')
    budgetsDiv.appendChild(getBudgetElement(window.data.currentBudget, true))
  }

  for (const budget of budgets) {
    budgetsDiv.appendChild(getBudgetElement(budget))
  }

  console.log(budgets)
}

function clearExpense () {
  document.getElementById('new-expense-name').value = ''
  document.getElementById('new-expense-value').value = ''
  document.getElementById('new-expense-submit').classList.add('disabled')
}

function validateNewExpense () {
  let name = document.getElementById('new-expense-name').value
  let value = parseFloat(document.getElementById('new-expense-value').value)

  if (name === '' || isNaN(value) || value <= 0) {
    return
  }

  return { name, value }
}

function onNewExpenseChange () {
  if (validateNewExpense()) {
    document.getElementById('new-expense-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-expense-submit').classList.add('disabled')
  }
}

function createExpense () {
  let data = validateNewExpense()
  if (!data) {
    // shouldn't happen
    return
  }

  const spent = window.data.currentBudget.expenses.reduce((acc, e) => acc + e.value, 0)
  const overload = spent + data.value > window.data.currentBudget.budget

  let question = `Deseja adicionar uma despesa no valor de ${numberWithSpaces(data.value)} €?`
  if (overload) {
    question += ` Irá exceder o orçamento definido de ${numberWithSpaces(window.data.currentBudget.budget)} €.`
  }

  confirmationBox({
    question: question,
    rightClass: overload ? 'cancel' : 'ok',
    rightHandler: () => {
      window.data.currentBudget.expenses.push({ ...data })
      runAndBack(clearExpense)
    }
  })
}

function getBudget (id) {
  if (window.data.currentBudget && window.data.currentBudget.id === id) {
    return window.data.currentBudget
  }

  for (const budget of window.data.budgets) {
    if (budget.id === id) return budget
  }
}

function showBudget (screen, id) {
  const { name, budget, expenses } = getBudget(id)
  const isCurrent = window.data.currentBudget && window.data.currentBudget.id === id

  screen.querySelectorAll('.only-current').forEach(el => {
    if (isCurrent) el.classList.remove('dn')
    else el.classList.add('dn')
  })

  updateScreenName(name)

  const expensesDiv = screen.querySelector('#budget-expenses')
  expensesDiv.innerHTML = ''

  let spent = 0
  for (const { name, value } of expenses) {
    let el = document.createElement('div')
    el.classList.add('expense')
    el.innerHTML = `<div>
        <p>${name}</p>
        <p>Despesa: ${numberWithSpaces(value)} €</p>
      </div>`

    spent += value
    expensesDiv.appendChild(el)
  }

  screen.querySelector('#budget-bar #budget-spent').innerHTML = numberWithSpaces(spent)
  screen.querySelector('#budget-bar #budget-max').innerHTML = numberWithSpaces(budget)

  if (spent > budget) {
    screen.querySelector('#budget-bar div').classList.add('excess')
  } else {
    screen.querySelector('#budget-bar div').classList.remove('excess')
    screen.querySelector('#budget-bar div').style.width = `${spent / budget * 100}%`
  }
}

function contactless () {
  const what = window.prompt('Insira o Vendedor')
  if (!what) return
  const howMuch = parseFloat(window.prompt('Insira o Valor da Compra'))
  if (isNaN(howMuch)) return

  confirmationBox({
    question: `Confirma a compra no valor de ${numberWithSpaces(howMuch)} € no ${what}?`,
    rightHandler: () => {
      if (!window.data.currentBudget) {
        return
      }

      window.data.currentBudget.expenses.push({
        name: what,
        value: howMuch
      })
    }
  })
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

  screen.innerHTML = ""

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

  document.querySelectorAll('.goto').forEach(el => {
    el.addEventListener('click', event => {
      event.preventDefault()

      if (el.dataset.needsunlock) {
        if (!history.find(el => el.name === 'mainmenu')) return
      }

      showScreen(el.dataset.to, el)
    })
  })

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
