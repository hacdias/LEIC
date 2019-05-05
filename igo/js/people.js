/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

function updatePeople (screen) {
  const people = window.data.people.sort(distanceSort)
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  for (const { name, id, distance, picture } of people) {
    let el = getListTemplate(screen)

    el.dataset.args = id
    el.querySelector('.picture').src = `assets/people/${picture}`
    el.querySelector('.name').innerHTML = name
    el.querySelector('.distance').innerHTML = distance

    content.appendChild(el)
  }

  enableGoto(content)
}

function updatePersonDetails (screen, id) {
  const friend = window.data.people.find(p => p.id === id)
  updateScreenName(friend.name)

  screen.querySelector('.name').innerHTML = friend.name
  screen.querySelector('.picture').src = `assets/people/${friend.picture}`
  screen.querySelector('.phone').innerHTML = friend.phone
  screen.querySelector('.distance').innerHTML = friend.distance + 'm'

  console.warn('TODO: add new contact')
}

function fillMessages (screen, id) {
  console.warn('TODO: Fill previous emssages, allow send new message')
}

function fillCalls (screen, id) {
  console.warn('TODO: Fill previous calls, add call button')
}
