/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

function getPerson (id) {
  return window.data.people.find(p => p.id === id)
}

function personPic (url) {
  return url.startsWith('data')
    ? url
    : `assets/people/${url}`
}

function updatePeople (screen) {
  const people = window.data.people.sort(distanceSort)
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  for (const { name, id, distance, picture } of people) {
    let el = getListTemplate(screen)

    el.dataset.args = id
    el.querySelector('.picture').src = personPic(picture)
    el.querySelector('.name').innerHTML = name
    el.querySelector('.distance').innerHTML = distance

    content.appendChild(el)
  }

  enableGoto(content)
}

function updatePersonDetails (screen, id) {
  const friend = window.data.people.find(p => p.id === id)
  updateScreenName(friend.name)

  screen.querySelector('.picture').src = personPic(friend.picture)
  screen.querySelector('.name').innerHTML = friend.name
  screen.querySelector('.phone').innerHTML = friend.phone
  screen.querySelector('.distance').innerHTML = friend.distance + 'm'
  screen.querySelectorAll('button').forEach(btn => { btn.dataset.args = id })
}

function checkNewMessages (pId) {
  let person = getPerson(pId)
  let el = document.getElementById('gotmessage')
  let people = window.data.people

  person.gotMessage = false
  el.classList.remove('visible')

  for (const key in people) {
    if (people[key].gotMessage == true && people[key].id != pId) {
      el.classList.add('visible')
      break
    }
  }
}

function fillMessages (screen, id) {
  const person = getPerson(id)
  screen.querySelector('input').value = ''
  const content = screen.querySelector('.content')
  content.innerHTML = ''
  checkNewMessages(id)

  for (const { message, from } of person.messages) {
    let el = getListTemplate(screen)

    if (from) el.classList.add('from')

    el.querySelector('.picture').src = `assets/people/${person.picture}`
    el.querySelector('.message').innerHTML = message

    content.appendChild(el)
  }

  content.scrollTo(0, content.scrollHeight)
  screen.querySelector('.input-with-icon span').dataset.args = id

  onMessageInput(screen.querySelector('.input-with-icon input'))
}

function replyMessage (el) {
  const id = el.dataset.args
  const input = el.parentElement.querySelector('input')
  const screen = el.parentElement.parentElement

  const person = getPerson(id)
  person.messages.push({ message: input.value })

  fillMessages(screen, id)
}

function validateNewPerson () {
  const name = document.getElementById('new-person-name').value
  const number = document.getElementById('new-person-phone').value

  if (name.length > 0 && name.length <= 16 && number > 0 && number < 10) {
    return { name, number }
  }
}

function onNewPersonChange () {
  if (validateNewPerson()) {
    document.getElementById('new-person-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-person-submit').classList.add('disabled')
  }
}

function createPerson () {
  const { name, number } = validateNewPerson()

  confirmationBox({
    question: 'Confirma a criação de um novo contacto?',
    rightHandler: () => {
      data.people.push({
        id: uuidv4(),
        name: name,
        distance: Math.floor(Math.random() * 1000) + 1,
        picture: generateAvatar(),
        phone: numberWithSpaces(number),
        lastSeen: Math.floor(Math.random() * 60) + 1,
        messages: []
      })

      runAndBack(clearNewPerson)
    }
  })
}

function deletePerson (el) {
  const person = getPerson(el.dataset.args)

  confirmationBox({
    question: `Tem a certeza que deseja apagar o contacto "${person.name}"?`,
    rightClass: 'cancel',
    rightHandler: () => {
      const index = data.people.findIndex(el => el.id === person.id)
      data.people.splice(index, 1)
      runAndBack()
    }
  })
}

function callContact (el) {
  const person = getPerson(el.dataset.args)

  confirmationBox({
    question: `Deseja ligar a ${person.name}?`,
    rightHandler: () => {
      showScreen('call', el)
    }
  })
}

function fillCall (screen, id) {
  const person = getPerson(id)

  screen.querySelector('.picture').src = `assets/people/${person.picture}`
  screen.querySelector('.name').innerHTML = person.name

  const end = () => {
    confirmationBox({
      question: 'Deseja terminar a chamada?',
      rightHandler: () => {
        document.querySelector('[data-to=--back]').removeEventListener('click', end)
        screen.querySelector('button').removeEventListener('click', end)
        runAndBack()
        runAndBack()
      }
    })
  }

  document.querySelector('[data-to=--back]').addEventListener('click', end)
  screen.querySelector('button').addEventListener('click', end)
}

function receiveCall (screen, id) {
  const person = getPerson(id)

  screen.querySelector('.picture').src = `assets/people/${person.picture}`
  screen.querySelector('.name').innerHTML = person.name

  const accept = () => {
    let el = document.createElement('x')
    el.dataset.args = person.id
    showScreen('call', el)
  }

  screen.querySelector('.ok').addEventListener('click', accept)

  const end = () => {
    confirmationBox({
      question: 'Deseja rejeitar a chamada?',
      rightHandler: () => {
        document.querySelector('[data-to=--back]').removeEventListener('click', end)
        screen.querySelector('button').removeEventListener('click', end)
        runAndBack()
      }
    })
  }

  document.querySelector('[data-to=--back]').addEventListener('click', end)
  screen.querySelector('.cancel').addEventListener('click', end)
}

function onMessageInput (el) {
  const div = el.parentElement
  if (div.querySelector('input').value !== '') {
    div.querySelector('span').classList.remove('disabled')
  } else {
    div.querySelector('span').classList.add('disabled')
  }
}

function clearNewPerson () {
  document.getElementById('new-person-submit').classList.add('disabled')
  document.getElementById('new-person-name').value = ''
  document.getElementById('new-person-phone').value = ''
}

function bootstrapPeople () {
  enableKeybaordFor(document.getElementById('msg-kb'))
  enableKeybaordFor(document.getElementById('new-person-name'))
  enableKeybaordFor(document.getElementById('new-person-phone'), false)
}

function seePersonInMap (el) {
  const person = getPerson(el.dataset.args)

  confirmationBox({
    question: `Deseja ir ter com: ${person.name}?`,
    rightHandler: () => {
      showScreen('gps-path', el)
    }
  })
}

function validateMoneyAmount () {
  const number = document.getElementById('lend-money-amount').value

  if (number > 0 && number <= 100) {
    return { number }
  }
}

function onMoneyChange () {
  if (validateMoneyAmount()) {
    document.getElementById('lend-money-submit').classList.remove('disabled')
  } else {
    document.getElementById('lend-money-submit').classList.add('disabled')
  }
}

function clearLendMoney () {
  document.getElementById('lend-money-submit').classList.add('disabled')
  document.getElementById('lend-money-amount').value = ''
}

function sharePlace (screen) {
  updateScreenName('Recomendar Local')
}

function fillSharePlacesList (screen, kind, title) {
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

    infoIcon.classList.add('fa-ruler')
    info.innerHTML = `${place.distance}m`

    const end = () => {
      confirmationBox({
        question: `Deseja recomendar: ${place.name}?`,
        rightHandler: () => {
          runAndBack()
          runAndBack()
        }
      })
    }

    el.addEventListener('click', end)
    content.appendChild(el)
  }
}

function lendMoney (el) {
  let data = validateMoneyAmount()
  if (!data) {
    // this shouldn't happen, but we never know!
    return
  }

  confirmationBox({
    question: `Confirma o empréstimo de ${numberWithSpaces(data.number)}€.`,
    rightHandler: () => {
      if (window.data.currentBudget) {
        window.data.currentBudget.expenses.push({
          name: 'Empréstimo',
          value: parseFloat(data.number)
        })
      }
      runAndBack(clearLendMoney)
    }
  })
}

function simulateCall (form) {
  let person = getContactFromForm(form)
  let el = document.createElement('x')
  el.dataset.args = person.id
  showScreen('calling', el)
}

function simulateMessage (form) {
  let person = getContactFromForm(form)
  let text = form.querySelector('input[type="text"]').value

  if (text == '') {
    return
  }

  person.messages.push({
    from: true,
    message: text
  })
  person.gotMessage = true
  let messages = document.getElementById('messages')
  if (messages.classList.contains('screen') && messages.classList.contains('active')) {
    fillMessages(messages, person.id)
  } else {
    let el = document.getElementById('gotmessage')
    el.classList.add('visible')
  }
}

function simulateRecommendation (form) {
  var restaurants = window.data.places.restaurants
  var monuments = window.data.places.monuments
  var markets = window.data.places.markets
  var diversions = window.data.places.diversions
  var parks = window.data.places.parks
  var all = restaurants.concat(monuments, markets, diversions, parks)
  var added = false
  var random = Math.floor(Math.random() * all.length)

  let recommendation = all[random]
  let person = getContactFromForm(form)

  confirmationBox({
    question: `${person.name} recomendou ${recommendation.name}. Pretende adicionar aos recomendados?`,
    rightHandler: () => {
      if (window.data.recommended.includes(recommendation)) {
        return
      }

      window.data.recommended.push(recommendation)
    }
  })
}

function peopleBootstrap () {
  enableKeybaordFor(document.getElementById('lend-money-amount'), false)
}
